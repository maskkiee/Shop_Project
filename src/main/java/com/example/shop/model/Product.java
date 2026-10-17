package com.example.shop.model;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public abstract class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        this.id = requireNonNull(id, "ID nie może być puste");
        this.name = requireNonNull(name, "Imie nie może być puste");
        if (price < 0) {
            throw new IllegalArgumentException("Cena nie może być ujemna");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Ilość nie może być ujemna");
        }
        this.price = price;
        this.stock = stock;
    }

    public abstract ProductType type();

    public void reduceStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ilość musi być dodatnia: " + amount);
        }
        if (amount > stock) {
            throw new OutOfStockException(name, amount, stock);
        }
        stock -= amount;
    }
}
