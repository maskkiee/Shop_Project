package com.example.shop.model;

import java.math.BigDecimal;

public class Electronics extends Product {

    public Electronics(int id, String name, BigDecimal price, int stock) {
        super(id, name, price, stock);
    }

    @Override
    public ProductType type() {
        return ProductType.ELECTRONICS;
    }
}
