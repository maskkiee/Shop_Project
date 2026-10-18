package com.example.shop.exceptions;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String ProductName, int requested, int available) {
        super(String.format("Brak ilość na magazynie %s (Żądano: %d, Dostępne: %d", ProductName, requested, available));
    };
}
