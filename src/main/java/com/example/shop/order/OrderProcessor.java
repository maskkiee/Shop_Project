package com.example.shop.order;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartItem;
import com.example.shop.exceptions.OrderProcessingException;
import com.example.shop.exceptions.OutOfStockException;
import com.example.shop.model.Customer;
import com.example.shop.model.Product;
import com.example.shop.persistence.OrderRepository;
import com.example.shop.promo.PromoService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.example.shop.support.OrderMapping.toOrderItems;
import static java.util.Objects.requireNonNull;

public class OrderProcessor {

    private static final String ORDER_PREFIX = "ORD-";

    private final InvoiceGenerator invoiceGenerator;
    private final OrderRepository repository;
    private final AtomicInteger sequence = new AtomicInteger();
    private final ExecutorService executorService;
    private PromoService promoService;

    public OrderProcessor(InvoiceGenerator invoiceGenerator, OrderRepository repository) {
        this.invoiceGenerator = requireNonNull(invoiceGenerator, "Invoice Generator cannot be null");
        this.repository = requireNonNull(repository, "Repository cannot be null");
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public OrderProcessor(InvoiceGenerator invoiceGenerator, OrderRepository repository, PromoService promoService) {
        this.invoiceGenerator = requireNonNull(invoiceGenerator, "Invoice Generator cannot be null");
        this.repository = requireNonNull(repository, "Repository cannot be null");
        this.promoService = requireNonNull(promoService, "Promo Service cannot be null");
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public Order process (Cart cart, Customer customer, String promoCode){
        double total = promoService.applyTo(promoCode, cart.getTotal());
        return createOrder(cart, customer, total);
    }

    public Order process (Cart cart, Customer customer){
        return createOrder(cart, customer, cart.getTotal());
    }

    private Order createOrder(Cart cart, Customer customer, double total) {
        if (cart.isEmpty()) {
            throw new OrderProcessingException("Cart is empty");
        }
        reserveStock(cart);
        emulateExternalWork();
        Order order = new Order(nextId(), customer, toOrderItems(cart), total,
                OffsetDateTime.now(Order.SHOP_ZONE));
        repository.save(order, invoiceGenerator.generate(order));
        cart.clear();
        return order;
    }

    public void processAsync(List<Cart> carts, List<Customer> customers){
        List<CompletableFuture<Order>> futures = IntStream.range(0, carts.size())
                .mapToObj(i -> CompletableFuture.supplyAsync(() ->
                        process(carts.get(i), customers.get(i)), executorService))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void ensureAvailable(CartItem item) {
        Product product = item.getProduct();
        if (product.getStock() < item.getQuantity()) {
            throw new OutOfStockException(product.getName(), item.getQuantity(), product.getStock());
        }
    }

    private void reserveStock(Cart cart) {
        cart.getItems().forEach(this::ensureAvailable);
        cart.getItems().forEach(item -> item.getProduct().reduceStock(item.getQuantity()));
    }

    private void emulateExternalWork(){
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String nextId() {
        return ORDER_PREFIX + String.format("%03d", sequence.incrementAndGet());
    }
}