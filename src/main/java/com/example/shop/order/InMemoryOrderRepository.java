package com.example.shop.order;

import com.example.shop.persistance.OrderRepository;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {

    private final List<Order> orders = new ArrayList<>();
    private final Map<String, String> invoices = new HashMap<>();

    @Override
    public synchronized void save(Order order, String invoice) {
        orders.add(order);
        invoices.put(order.getId(), invoice);
    }

    public synchronized List<Order> findAll() {
        return List.copyOf(orders);
    }

    public synchronized Optional<String> findInvoice(String orderId) {
        return Optional.ofNullable(invoices.get(orderId));
    }
}