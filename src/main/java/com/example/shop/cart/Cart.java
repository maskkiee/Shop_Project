package com.example.shop.cart;

import com.example.shop.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Cart {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        requireNonNull(product, "Product cannot be null");
        product.ensureReadyForCart();
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
        findItem(product).ifPresentOrElse(item -> item.addQuantity(quantity),
                () -> items.add(new CartItem(product, quantity)));
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public double getTotal() {
        return items.stream().mapToDouble(item ->
                item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    public void clear() {
        items.clear();
    }

    public Optional<CartItem> findItem(Product product) {
        return items.stream().filter(item -> item.getProduct() == product).findFirst();
    }
}
