package com.example.shop.model;

public record Customer(String name, String email) {

    public Customer{
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.isEmpty()){
            throw new IllegalArgumentException("E-mail cannot be null or empty");
        }
        if (!email.contains("@")){
            throw new IllegalArgumentException("E-mail in invalid form (bez @)");
        }
    }
}
