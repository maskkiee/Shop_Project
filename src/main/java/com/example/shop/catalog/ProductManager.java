package com.example.shop.catalog;

import com.example.shop.exceptions.ProductNotFoundException;
import com.example.shop.model.Product;
import com.example.shop.model.ProductType;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class ProductManager {

    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public void addProduct(Product product) {
        requireNonNull(product, "Product nie może być pusty");
        if (products.putIfAbsent((product.getId()), product) != null) {
            throw new IllegalArgumentException("Produkt już istnieje: " + product.getId());
        }
    }

    public void removeProduct(int productID) {
        Product removed = products.remove(productID);
        if (removed == null) {
            throw new ProductNotFoundException(productID);
        }
    }

    public void update(Product updated) {
        requireNonNull(updated, "Produkt nie może być null");
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

