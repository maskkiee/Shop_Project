package com.example.shop.CLI;

import com.example.shop.cart.Cart;
import com.example.shop.model.Customer;
import com.example.shop.model.Product;
import com.example.shop.model.ProductManager;
import com.example.shop.order.Order;
import com.example.shop.order.OrderProcessor;

import java.util.Scanner;

public class ShopCli {

    private static final String MENU = """
             --- MENU ---
            1. Show products
            2. Add product to cart
            3. Show cart
            4. Place an order
            0. Exit
            """;

    private final Scanner scanner;
    private final ProductManager manager;
    private final Cart cart;
    private final OrderProcessor processor;

    public ShopCli(Scanner scanner, ProductManager manager, Cart cart, OrderProcessor processor) {
        this.scanner = scanner;
        this.manager = manager;
        this.cart = cart;
        this.processor = processor;
    }

    public void run() {
        boolean running = true;
        while (running) {
            System.out.print(MENU);
            running = handleChoice(readLine());
        }
    }

    private boolean handleChoice(String choice) {
        switch (choice) {
            case "1" -> showProducts();
            case "2" -> addProductToCart();
            case "3" -> showCart();
            case "4" -> checkout();
            case "0" -> {
                System.out.println("Goodbye!");
                return false;
            }
            default -> System.out.println("Unknown option: " + choice);
        }
        return true;
    }

    private void showProducts() {
        manager.findAll().forEach(this::printProduct);
    }

    private void printProduct(Product product) {
        System.out.printf("%-14s [%s] %8.2f zł  stock: %d%n",
                product.getId(), product.type(), product.getPrice(), product.getStock());
    }

    private void addProductToCart() {
        System.out.println("Please enter the product ID: ");
        int productId = Integer.parseInt(readLine());
        Product product = manager.findByID(productId).orElse(null);
        if (product == null) {
            System.out.println("Product with ID: " + productId + " not found");
            return;
        }
        try {
            cart.addItem(product, readQuantity());
            System.out.println("Product " + productId + " added to cart.");
        } catch (RuntimeException e) {
            System.out.println("Product with ID: " + productId + " cannot be added");
        }
    }

    private void showCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        cart.getItems().forEach(item -> System.out.printf("  %s x%d = %.2f zł%n",
                item.getProduct().getName(), item.getQuantity(),
                item.getProduct().getPrice() * item.getQuantity()));
        System.out.printf("Total: %.2f zł%n", cart.getTotal());
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        Customer customer = new Customer(promptText("Name: "), promptText("Email: "));
        try {
            Order order = processor.process(cart, customer);
            System.out.printf("Order: %s Total: %.2f zł%n",
                    order.getId(), order.getTotal());
        } catch (RuntimeException e) {
            System.out.println("Order couldn't be completed: " + e.getMessage());
        }
    }

    private int readQuantity() {
        System.out.println ("Please enter the quantity to checkout: ");
        return Integer.parseInt(readLine());
    }

    private String promptText(String label) {
        System.out.print(label);
        return readLine();
    }

    private String readLine() {
        return scanner.nextLine().trim();
    }
}
