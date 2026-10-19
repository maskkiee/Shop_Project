package com.example.shop.model;

import com.example.shop.exceptions.OutOfStockException;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class Product {

    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        this.id = requireNonNull(id, "ID cannot be null");
        this.name = requireNonNull(name, "Name cannot be null");
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.price = price;
        this.stock = stock;
    }

    public abstract ProductType type();

    public void ensureReadyForCart() {
        if (stock <= 0) {
            throw new IllegalStateException("Out of stock");
        }
    }

    public void reduceStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + amount);
        }
        if (amount > stock) {
            throw new OutOfStockException(name, amount, stock);
        }
        stock -= amount;
    }
}