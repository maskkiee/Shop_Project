package com.example.shop.model;

public record Customer(String name, String email) {
    public Customer{
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Imie klienta nie może być puste");
        }
        if (email == null || email.isEmpty()){
            throw new IllegalArgumentException("E-mail klienta nie może być pusty");
        }
        if (!email.contains("@")){
            throw new IllegalArgumentException("E-mail w niepoprawnej formie (bez @)");
        }
    }
}
