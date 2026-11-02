package com.example.shop.order;

import java.math.BigDecimal;

public record OrderItem(String productId, String name, BigDecimal unitPrice, int quantity) {

    public OrderItem {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
