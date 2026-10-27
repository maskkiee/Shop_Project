package com.example.shop.files;

import com.example.shop.exceptions.OrderProcessingException;
import com.example.shop.order.Order;
import com.example.shop.persistance.OrderRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;

public class FileOrderRepository implements OrderRepository {

    private static final String INVOICE_PREFIX = "invoice-";
    private static final String INVOICE_SUFFIX = ".txt";

    private final Path ordersFile;
    private final Path invoiceDirectory;

    public FileOrderRepository(Path ordersFile, Path invoiceDirectory) {
        this.ordersFile = requireNonNull(ordersFile, "Orders file cannot be null");
        this.invoiceDirectory = requireNonNull(invoiceDirectory, "Invoice directory cannot be null");
    }

    @Override
    public void save(Order order, String invoice) {
        try {
            Files.createDirectories(invoiceDirectory);
            Files.writeString(ordersFile, toLine(order) + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
            Files.writeString(invoicePath(order), invoice, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new OrderProcessingException(
                    "Couldn't save order " + order.getId(), e);
        }
    }

    private Path invoicePath(Order order) {
        return invoiceDirectory.resolve(INVOICE_PREFIX + order.getId() + INVOICE_SUFFIX);
    }

    private String toLine(Order order) {
        return String.join("|",
                order.getId(),
                order.getCustomer().email(),
                String.format("%.2f", order.getTotal()),
                order.getCreatedAt().toString());
    }
}