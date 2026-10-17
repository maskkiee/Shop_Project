package com.example.shop.model;

import java.util.List;


import static java.util.Objects.requireNonNull;

public record SmartphoneSpec(String color, int batteryCapacity, List<String> accesories) {
    public SmartphoneSpec {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Kolor nie może być null ani pusty");
        }
        if (batteryCapacity < 1) {
            throw new IllegalArgumentException("Pojemność baterii jest poza zakresem: " + batteryCapacity);
        }
        accesories = List.copyOf(requireNonNull(accesories, "Accessories nie może być null ani pusty"));
    }
}
