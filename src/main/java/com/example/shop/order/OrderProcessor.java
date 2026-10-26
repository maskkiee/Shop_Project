package com.example.shop.order;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartItem;
import com.example.shop.exceptions.OrderProcessingException;
import com.example.shop.exceptions.OutOfStockException;
import com.example.shop.model.Customer;
import com.example.shop.model.Product;
import com.example.shop.persistance.OrderRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.shop.support.OrderMapping.toOrderItems;
import static java.util.Objects.requireNonNull;

public class OrderProcessor {

    private static final String ORDER_PREFIX = "ORD-";

    private final InvoiceGenerator invoiceGenerator;
    private final OrderRepository repository;
    private final AtomicInteger sequence = new AtomicInteger();

    public OrderProcessor(InvoiceGenerator invoiceGenerator, OrderRepository repository) {
        this.invoiceGenerator = requireNonNull(invoiceGenerator, "Invoice Generator cannot be null");
        this.repository = requireNonNull(repository, "Repository cannot be null");
    }

    public Order process(Cart cart, Customer customer){
        if (cart.isEmpty()) {
            throw new OrderProcessingException("Cart is empty");
        }
        reserveStock(cart);
        Order order = new Order(nextId(), customer, toOrderItems(cart), cart.getTotal(),
                OffsetDateTime.now(Order.SHOP_ZONE));
        repository.save(order, invoiceGenerator.generate(order));
        cart.clear();
        return order;
        }

    private void reserveStock(Cart cart) {
        cart.getItems().forEach(this::ensureAvailable);
        cart.getItems().forEach(item -> item.getProduct().reduceStock(item.getQuantity()));
    }

    private void ensureAvailable(CartItem item) {
        Product product = item.getProduct();
        if (product.getStock() < item.getQuantity()) {
            throw new OutOfStockException(product.getName(), item.getQuantity(), product.getStock());
        }
    }

    private String nextId() {
        return ORDER_PREFIX + String.format("%03d", sequence.incrementAndGet());
    }
}