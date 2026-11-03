package com.example.shop.model;

public record ComputerSpec(String cpu, int ramGB, int storageGB) {

    public ComputerSpec {
        if (cpu == null || cpu.isBlank()) {
            throw new IllegalArgumentException("CPU cannot be null or blank");
        }
        if (ramGB < 1) {
            throw new IllegalArgumentException("Ram GB cannot be less than 1GB");
        }
        if (storageGB < 1) {
            throw new IllegalArgumentException("Storage GB cannot be less than 1GB");
        }
    }
}
