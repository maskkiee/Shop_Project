package com.example.shop.model;

public record ComputerSpec(String cpu, int ramGB, int storageGB) {

    public ComputerSpec {
        if (cpu == null || cpu.isBlank()) {
            throw new IllegalArgumentException("CPU nie może być pusty ani null");
        }
        if (ramGB < 1) {
            throw new IllegalArgumentException("Ram GB nie może być mniejszy niż 1GB");
        }
        if (storageGB < 1) {
            throw new IllegalArgumentException("Storage GB nie może być mniejszy niż 1GB");
        }

    }
}
