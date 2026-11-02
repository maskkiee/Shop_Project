package com.example.shop.support;

import com.example.shop.cart.Cart;
import com.example.shop.model.*;
import com.example.shop.promo.PromoService;

import java.math.BigDecimal;

public final class TestProducts {

    private TestProducts() {
    }

    public static Smartphone smartphones(int id, BigDecimal price, int stock) {
        return new Smartphone(id, "Product " + id, price, stock);
    }

    public static Electronics electronics(int id, BigDecimal price, int stock) {
        return new Electronics(id, "Product " + id, price, stock);
    }

    public static Computer computers(int id, BigDecimal price, int stock) {
        return new Computer(id, "Computer " + id, price, stock);
    }

    public static Customer customers() {
        return new Customer("Anna Nowak", "anowak@gmail.com");
    }

    public static PromoService samplePromos() {
        PromoService promos = new PromoService();
        promos.register("TEST10", BigDecimal.valueOf(0.10));
        return promos;
    }

    public static Cart addToCart(Product product, int quantity) {
        Cart cart = new Cart();
        cart.addItem(product, quantity);
        return cart;
    }
}