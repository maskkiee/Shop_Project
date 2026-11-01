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
import com.example.shop.support.TestProducts;

import java.nio.file.Paths;
import java.util.Scanner;

public class ShopApplication {

    private ShopApplication() {
    }

    public static void main(String[] args) {
        var ordersFile = Paths.get("orders.txt");
        var invoiceDir = Paths.get("invoices");
        PromoService promoService = TestProducts.samplePromos();
        OrderRepository repository = new FileOrderRepository(ordersFile, invoiceDir);
        OrderProcessor processor = new OrderProcessor(new InvoiceGenerator(), repository, promoService);
        try (Scanner scanner = new Scanner(System.in)) {
            new ShopCli(scanner, sampleCatalog(), new Cart(), processor).run();
        }
    }

    private static ProductManager sampleCatalog() {
        ProductManager manager = new ProductManager();
        manager.addProduct(new Computer(1, "Computer", 7999.0, 5));
        manager.addProduct(new Smartphone(2, "Smartphone", 3499.0, 12));
        manager.addProduct(new Electronics(3, "Headphones", 299.0, 30));
        manager.addProduct(new Electronics(4, "Keyboard", 449.0, 18));
        return manager;
    }
}
