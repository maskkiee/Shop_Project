package com.example.shop.persistance;

import com.example.shop.order.Order;

public interface OrderRepository {

    void save(Order order, String invoice);
}