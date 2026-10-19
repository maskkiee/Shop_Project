package com.example.shop.model;

import java.util.List;


import static java.util.Objects.requireNonNull;

public record SmartphoneSpec(String color, int batteryCapacity, List<String> accesories) {

    public SmartphoneSpec {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (batteryCapacity < 1) {
            throw new IllegalArgumentException("Battery capacity is out of range: " + batteryCapacity);
        }
        accesories = List.copyOf(requireNonNull(accesories, "Accessories cannot be null"));
    }
}
