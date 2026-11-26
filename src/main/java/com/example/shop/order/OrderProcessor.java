package com.example.shop.order;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartItem;
import com.example.shop.exceptions.OrderProcessingException;
import com.example.shop.exceptions.OutOfStockException;
import com.example.shop.model.Customer;
import com.example.shop.model.Product;
import com.example.shop.persistence.OrderRepository;
import com.example.shop.promo.PromoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.example.shop.support.OrderMapping.toOrderItems;
import static java.util.Objects.requireNonNull;

/**
 * Przetwarza koszyk na zamówienie: walidacja, rezerwacja stanu, rabat, generowanie faktury, procesowanie zanówienia.
 * Rzuca wyjątki OutOfStockException gdy brakuje produktów na stanie, oraz OrderProcessingException gdy koszyk jest pusty.
 * Metoda process - przetwarza jedno zamówienie naraz, processAsync przetwarza wiele asynchronicznie.
 * Po sukcesie czyści koszyk.
 */
public class OrderProcessor implements AutoCloseable {

    private static final String ORDER_PREFIX = "ORD-";

    private final InvoiceGenerator invoiceGenerator;
    private final OrderRepository repository;
    private final AtomicInteger sequence = new AtomicInteger();
    private final ExecutorService executorService;
    private final PromoService promoService;

    private final List<OrderObserver> observers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Order order) {
        observers.forEach(observer -> observer.onOrderPlaced(order));
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    public OrderProcessor(InvoiceGenerator invoiceGenerator, OrderRepository repository) {
        this(invoiceGenerator, repository, new PromoService());
    }

    public OrderProcessor(InvoiceGenerator invoiceGenerator, OrderRepository repository, PromoService promoService) {
        this.invoiceGenerator = requireNonNull(invoiceGenerator, "Invoice Generator cannot be null");
        this.repository = requireNonNull(repository, "Repository cannot be null");
        this.promoService = requireNonNull(promoService, "Promo Service cannot be null");
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Order process(Cart cart, Customer customer, String promoCode) {
        BigDecimal total = promoService.applyTo(promoCode, cart.getTotal());
        return createOrder(cart, customer, total);
    }

    public Order process(Cart cart, Customer customer) {
        return createOrder(cart, customer, cart.getTotal());
    }

    //Builder
    private Order createOrder(Cart cart, Customer customer, BigDecimal total) {
        if (cart.isEmpty()) {
            throw new OrderProcessingException("Cart is empty");
        }
        Order order = Order.builder()
                .id(nextId())
                .customer(customer)
                .items(toOrderItems(cart))
                .total(total)
                .createdAt(OffsetDateTime.now(Order.SHOP_ZONE))
                .build();
        reserveStock(cart);
        emulateExternalWork();
        repository.save(order, invoiceGenerator.generate(order));
        notifyObservers(order);
        cart.clear();
        return order;
    }

    public void processAsync(List<Cart> carts, List<Customer> customers) {
        List<CompletableFuture<Order>> futures = IntStream.range(0, carts.size())
                .mapToObj(i -> CompletableFuture.supplyAsync(() ->
                        process(carts.get(i), customers.get(i)), executorService))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void processSync(List<Cart> carts, List<Customer> customers) {
        IntStream.range(0, carts.size())
                .forEach(i -> process(carts.get(i), customers.get(i)));
    }

    private void ensureAvailable(CartItem item) {
        Product product = item.getProduct();
        if (product.getStock() < item.getQuantity()) {
            throw new OutOfStockException(product.getName(), item.getQuantity(), product.getStock());
        }
    }

    private synchronized void reserveStock(Cart cart) {
        cart.getItems().forEach(this::ensureAvailable);
        cart.getItems().forEach(item -> item.getProduct().reduceStock(item.getQuantity()));
    }

    private void emulateExternalWork() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String nextId() {
        String time = OffsetDateTime.now(Order.SHOP_ZONE)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        return ORDER_PREFIX + time + "-" + String.format("%03d", sequence.incrementAndGet());
    }
}