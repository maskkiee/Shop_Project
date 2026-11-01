package com.example.shop.promo;

import java.util.HashMap;
import java.util.Map;

public class PromoService {

    private static final double FULL_PRICE = 1.00;
    private static final double NO_DISCOUNT = 0.00;

    private final Map<String, Double> promos = new HashMap<>();

    public void register(String code, double promo) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be blank");
        }
        if (promo >= FULL_PRICE || promo <= NO_DISCOUNT) {
            throw new IllegalArgumentException("Promo exceeds price limit");
        }
        promos.put(code.toUpperCase(), promo);
    }

    public double applyTo(String code, double total) {
        if (code == null || code.isBlank()) {
            return total;
        }
        double discount = promos.getOrDefault(code.toUpperCase(), NO_DISCOUNT);
        return total * (FULL_PRICE - discount);
    }
}
