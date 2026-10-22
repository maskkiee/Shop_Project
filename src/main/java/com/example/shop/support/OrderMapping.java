package com.example.shop.support;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartItem;
import com.example.shop.model.Product;
import com.example.shop.order.OrderItem;

import java.util.List;

public class OrderMapping {

    public static List<OrderItem> toOrderItems(Cart cart) {
        return cart.getItems().stream()
                .map(OrderMapping::toOrderItem)
                .toList();
    }

    private static OrderItem toOrderItem(CartItem item) {
        Product product = item.getProduct();
        return new OrderItem(String.valueOf(product.getId()), product.getName(), product.getPrice(), item.getQuantity());
    }
}
