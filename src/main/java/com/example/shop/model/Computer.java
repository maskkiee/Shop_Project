package com.example.shop.model;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Computer extends Product {

    private ComputerSpec spec;

    public Computer(int id, String name, double price, int stock) {
        super(id, name, price, stock);
    }

    @Override
    public ProductType type() {
        return ProductType.COMPUTER;
    }

    public void configure(ComputerSpec spec) {
        this.spec = requireNonNull(spec, "Specification cannot be null");
    }

    public Optional<ComputerSpec> getSpec() {
        return Optional.ofNullable(spec);
    }
}
