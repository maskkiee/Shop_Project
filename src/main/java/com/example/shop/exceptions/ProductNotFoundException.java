package com.example.shop.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final int productID;

    public ProductNotFoundException(int productID) {
        super("Produkt nie istnieje: " + productID);
        this.productID = productID;
    }
}
