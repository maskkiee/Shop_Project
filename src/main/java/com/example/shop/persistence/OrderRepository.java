package com.example.shop.persistence;

import com.example.shop.order.Order;

public interface OrderRepository {

    void save(Order order, String invoice);
}