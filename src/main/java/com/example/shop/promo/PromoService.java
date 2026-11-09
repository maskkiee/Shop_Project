package com.example.shop.promo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PromoService {

    private static final BigDecimal FULL_PRICE = BigDecimal.ONE;
    private static final BigDecimal NO_DISCOUNT = new BigDecimal("0.00");

    private final Map<String, BigDecimal> promos = new HashMap<>();

    //Strategy
    public void register(String code, BigDecimal promo) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code cannot be blank");
        }
        if (promo.compareTo(FULL_PRICE) >= 0 || promo.compareTo(NO_DISCOUNT) <= 0) {
            throw new IllegalArgumentException("Promo exceeds price limit");
        }
        promos.put(code.toUpperCase(), promo);
    }

    public BigDecimal applyTo(String code, BigDecimal total) {
        if (code == null || code.isBlank()) {
            return total;
        }
        BigDecimal discount = promos.getOrDefault(code.toUpperCase(), NO_DISCOUNT);
        return total.multiply(FULL_PRICE.subtract(discount));
    }
}
