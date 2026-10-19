package com.example.shop.exceptions;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String ProductName, int requested, int available) {
        super(String.format("Out of stock %s (Requested: %d, Available: %d", ProductName, requested, available));
    };
}
