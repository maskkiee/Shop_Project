package com.example.shop.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final int productId;

    public ProductNotFoundException(int productId) {
        super("Product doesn't exist: " + productId);
        this.productId = productId;
    }
}