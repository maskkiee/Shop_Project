package com.example.shop.model;

import java.util.List;


import static java.util.Objects.requireNonNull;

public record SmartphoneSpec(String color, int batteryCapacity, List<String> accessories) {

    public SmartphoneSpec {
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (batteryCapacity < 1) {
            throw new IllegalArgumentException("Battery capacity is out of range: " + batteryCapacity);
        }
        accessories = List.copyOf(requireNonNull(accessories, "Accessories cannot be null"));
    }
}
