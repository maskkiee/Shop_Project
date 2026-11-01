package com.example.shop.order;

import com.example.shop.model.Customer;
import lombok.Getter;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class Order {

    public static final ZoneId SHOP_ZONE =  ZoneId.of("Europe/Warsaw");

    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final double total;
    private final OffsetDateTime createdAt;

    public Order(String id, Customer customer, List<OrderItem> items, double total, OffsetDateTime createdAt) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        this.id = id;
        this.customer = requireNonNull(customer, "Customer cannot be null");
        this.items = List.copyOf(requireNonNull(items, "Items cannot be null"));
        if (total <= 0) {
            throw new IllegalArgumentException("Total cannot be negative: " + total);
        }
        this.total = total;
        this.createdAt = requireNonNull(createdAt, "CreatedAt cannot be null");
    }
}
