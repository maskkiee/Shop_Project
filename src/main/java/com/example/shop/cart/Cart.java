package com.example.shop.cart;

import com.example.shop.model.Product;

import java.math.BigDecimal;
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
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        findItem(product).ifPresentOrElse(item -> item.addQuantity(quantity),
                () -> items.add(new CartItem(product, quantity)));
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Optional<CartItem> findItem(Product product) {
        return items.stream().filter(item -> item.getProduct() == product).findFirst();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
