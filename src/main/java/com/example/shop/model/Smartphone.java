package com.example.shop.model;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Smartphone extends Product {

    private SmartphoneSpec spec;

    public Smartphone(int id, String name, BigDecimal price, int stock) {
        super(id, name, price, stock);
    }

    @Override
    public ProductType type() {
        return ProductType.SMARTPHONE;
    }

    public void configure(SmartphoneSpec spec) {
        this.spec = requireNonNull(spec, "Specification cannot be null");
    }

    public Optional<SmartphoneSpec> getSpec() {
        return Optional.ofNullable(spec);
    }
}
