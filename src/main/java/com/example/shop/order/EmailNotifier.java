package com.example.shop.order;

//Observer - new
public class EmailNotifier implements OrderObserver {
    @Override
    public void onOrderPlaced(Order order) {
        System.out.println("Confirmation Email: " + order.getCustomer().email());
    }
}