package com.example.shop.support;

import com.example.shop.model.*;

public final class TestProducts {

    private TestProducts() {
    }

    public static Smartphone smartphones(int id, double price, int stock) {
        return new Smartphone(id, "Product " + id, price, stock);
    }

    public static Electronics electronics(int id, double price, int stock) {
        return new Electronics(id, "Product " + id, price, stock);
    }

    public static Computer computers(int id, double price, int stock) {
        return new Computer(id, "Computer " + id, price, stock);
    }

    public static Customer customers() {
        return new Customer("Anna Nowak", "anowak@gmail.com");
    }
}