package com.example.shop.exceptions;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String productName, int requested, int available) {
        super(String.format("Out of stock %s (Requested: %d, Available: %d)", productName, requested, available));
    }
}