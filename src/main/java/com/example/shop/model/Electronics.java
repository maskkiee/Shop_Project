package com.example.shop.model;

public class Electronics extends Product{

    public Electronics(int id, String name, double price, int stock) {
        super(id, name, price, stock);
    }

    @Override
    public ProductType type() {
        return ProductType.ELECTRONICS;
    }
}
