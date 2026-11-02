package com.example.shop.app;

import com.example.shop.cli.ShopCli;
import com.example.shop.cart.Cart;
import com.example.shop.files.FileOrderRepository;
import com.example.shop.model.Computer;
import com.example.shop.model.Electronics;
import com.example.shop.model.ProductManager;
import com.example.shop.model.Smartphone;
import com.example.shop.order.InvoiceGenerator;
import com.example.shop.order.OrderProcessor;
import com.example.shop.persistence.OrderRepository;
import com.example.shop.promo.PromoService;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Scanner;

public class ShopApplication {

    private ShopApplication() {
    }

    public static void main(String[] args) {
        var ordersFile = Paths.get("orders.txt");
        var invoiceDir = Paths.get("invoices");
        PromoService promoService = promoServiceRegister("START10", new BigDecimal("0.1"));
        OrderRepository repository = new FileOrderRepository(ordersFile, invoiceDir);
        OrderProcessor processor = new OrderProcessor(new InvoiceGenerator(), repository, promoService);
        try (Scanner scanner = new Scanner(System.in)) {
            new ShopCli(scanner, sampleCatalog(), new Cart(), processor).run();
        }
    }

    private static ProductManager sampleCatalog() {
        ProductManager manager = new ProductManager();
        manager.add(new Computer(1, "Computer", new BigDecimal("7999.0"), 5));
        manager.add(new Smartphone(2, "Smartphone", new BigDecimal("7499.0"), 12));
        manager.add(new Electronics(3, "Headphones", new BigDecimal("299.0"), 30));
        manager.add(new Electronics(4, "Keyboard", new BigDecimal("449.0"), 18));
        return manager;
    }

    private static PromoService promoServiceRegister(String code, BigDecimal promoPercentage) {
        PromoService promoService = new PromoService();
        promoService.register(code, promoPercentage);
        return promoService;
    }
}
