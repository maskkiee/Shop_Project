package com.example.shop.TASK10;

import com.example.shop.cart.Cart;
import com.example.shop.files.FileOrderRepository;
import com.example.shop.model.Computer;
import com.example.shop.order.*;
import com.example.shop.promo.PromoService;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.shop.cart.Cart.addToCart;
import static com.example.shop.support.TestProducts.customers;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesTests {

    @TempDir
    Path tempDir;

    private OrderProcessor processor;

    @BeforeEach
    void setUp() {
        FileOrderRepository repository = new FileOrderRepository(
                tempDir.resolve("orders.txt"), tempDir.resolve("invoices"));
        processor = new OrderProcessor(new InvoiceGenerator(), repository);
    }

    @Test
    public void saveCreateOrderFileAndInvoiceFileWithCorrectContent() throws IOException {
        Computer computer = TestProducts.computers(1, 20, 50);
        Cart cart = addToCart(computer, 2);
        Order order = processor.process(cart, customers());

        List<String> lines = Files.readAllLines(tempDir.resolve("orders.txt"));
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0)).startsWith("ORD-001|anowak@gmail.com|40.00|");

        Path invoice = tempDir.resolve("invoices/invoice-" + order.getId() + ".txt");
        assertThat(invoice).exists();
        assertThat(Files.readString(invoice)).contains("Invoice: " + order.getId());
    }
}
