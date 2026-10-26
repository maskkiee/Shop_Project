package com.example.shop.model;

import com.example.shop.exceptions.ProductNotFoundException;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ProductManager {

    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public void addProduct(Product product) {
        requireNonNull(product, "Product cannot be null");
        if (products.putIfAbsent((product.getId()), product) != null) {
            throw new IllegalArgumentException("Product already exists: " + product.getId());
        }
    }

    public void removeProduct(int productID) {
        Product removed = products.remove(productID);
        if (removed == null) {
            throw new ProductNotFoundException(productID);
        }
    }

    public void update(Product updated) {
        requireNonNull(updated, "Produkt cannot be null");
        if (!products.containsKey(updated.getId())) {
            throw new ProductNotFoundException(updated.getId());
        }
        products.put(updated.getId(), updated);
    }

    public Optional<Product> findByID(int productID) {
        return Optional.ofNullable(products.get(productID));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> findByType(ProductType type) {
        return findAll().stream()
                .filter(product -> product.type() == type).toList();
    }
}

