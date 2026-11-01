package com.example.shop.TASK3;

import com.example.shop.cart.Cart;
import com.example.shop.cart.CartItem;
import com.example.shop.model.Computer;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CartTests {

    private final Cart cart = new Cart();

    @Test
    void addToCartCorrectlyAndFindsAndTotals() {
        Computer computer = TestProducts.computers(1, 20, 50);

        cart.addItem(computer, 1);

        assertThat(cart.findItem(computer)).isPresent();
        assertThat(cart.getTotal()).isEqualTo(20);
        assertThat(cart.getItems().size()).isEqualTo(1);
    }

    @Test
    void addsProductAndComputesTotal(){
        Computer computer = TestProducts.computers(1, 20, 50);

        cart.addItem(computer, 2);
        cart.addItem(computer, 4);

        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(6);
    }

    @Test
    void rejectsNotPositiveQuantity(){
        Computer computer = TestProducts.computers(1, 20, 50);

        assertThatThrownBy(() -> cart.addItem(computer, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity cannot be negative");
    }

    @Test
    void increasesQuantityWhenAddQuantityCalled() {
        Computer computer = TestProducts.computers(1, 20, 50);
        CartItem item = new CartItem(computer, 2);

        item.addQuantity(3);

        assertThat(item.getQuantity()).isEqualTo(5);
    }

    @Test
    void throwsAtNegativeQuantityWhenAddQuantityCalled() {
        Computer computer = TestProducts.computers(1, 20, 50);
        CartItem item = new CartItem(computer, 2);

        assertThatThrownBy(() -> item.addQuantity(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity has to be positive");
    }

    @Test
    void clearsCartCorrectly() {
        Computer computer = TestProducts.computers(1, 20, 50);
        cart.addItem(computer, 1);

        cart.clear();

        assertThat(cart.getItems().size()).isEqualTo(0);
    }
}
