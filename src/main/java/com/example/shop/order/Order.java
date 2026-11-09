package com.example.shop.order;

import com.example.shop.model.Customer;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class Order {

    public static final ZoneId SHOP_ZONE = ZoneId.of("Europe/Warsaw");

    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal total;
    private final OffsetDateTime createdAt;

    //Builder - new
    private Order(Builder b) {
        if (b.id == null || b.id.isBlank())
            throw new IllegalArgumentException("Order ID cannot be null");
        this.id = b.id;
        this.customer = requireNonNull(b.customer, "Customer cannot be null");
        this.items = List.copyOf(requireNonNull(b.items, "Items cannot be null"));
        if (b.total == null || b.total.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Total must be positive");
        this.total = b.total;
        this.createdAt = requireNonNull(b.createdAt, "Created at cannot be null");
    }
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private Customer customer;
        private List<OrderItem> items;
        private BigDecimal total;
        private OffsetDateTime createdAt;

        public Builder id(String id)                { this.id = id; return this; }
        public Builder customer(Customer c)         { this.customer = c; return this; }
        public Builder items(List<OrderItem> items) { this.items = items; return this; }
        public Builder total(BigDecimal total)      { this.total = total; return this; }
        public Builder createdAt(OffsetDateTime t)  { this.createdAt = t; return this; }

        public Order build() { return new Order(this); }
    }
}