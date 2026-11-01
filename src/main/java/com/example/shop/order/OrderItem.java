package com.example.shop.order;

public record OrderItem(String productID, String name, double unitPrice, int quantity) {

    public OrderItem {
        if  (productID == null || productID.isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public double totalPrice() {
        return unitPrice * quantity;
    }
}
