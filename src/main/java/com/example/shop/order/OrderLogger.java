package com.example.shop.order;

//Observer - new
public class OrderLogger implements OrderObserver {
    @Override
    public void onOrderPlaced(Order order) {
        System.out.println("Order saved: " + order.getId());
    }
}