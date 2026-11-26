package com.example.shop.cart;

import com.example.shop.model.Product;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        requireNonNull(product, "Product cannot be null");
        this.product = product;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity has to be positive: " + quantity);
        }
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity has to be positive: " + amount);
        }
        quantity += amount;
    }
}
