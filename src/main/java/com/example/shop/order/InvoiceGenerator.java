package com.example.shop.order;

import java.math.RoundingMode;
import java.util.stream.Collectors;

public class InvoiceGenerator {

    public String generate(Order order) {
        String header = "Invoice: " + order.getId() + "%n"
                + "Client: " + order.getCustomer().name()
                + " <" + order.getCustomer().email() + ">%n"
                + "Date: " + order.getCreatedAt() + "%n";
        String lines = order.getItems().stream()
                .map(InvoiceGenerator::formatItem)
                .collect(Collectors.joining("%n"));
        return header + lines + order.getTotal().setScale(2, RoundingMode.HALF_UP);
    }

    private static String formatItem(OrderItem item) {
        return String.format("- %s x%d = %.2f zł",
                item.name(), item.quantity(), item.totalPrice());
    }
}