package com.example.shop.TASK2;

import com.example.shop.model.ProductManager;
import com.example.shop.exceptions.ProductNotFoundException;
import com.example.shop.model.Computer;
import com.example.shop.model.Electronics;
import com.example.shop.model.ProductType;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductManagerTest {

    private final ProductManager manager = new ProductManager();

    @Test
    void productManagerAddsAndFinds() {
        Computer computer = TestProducts.computers(1, 20, 50);

        manager.addProduct(computer);

        assertThat(manager.findById(1)).contains(computer);
        assertThat(manager.findAll().size()).isEqualTo(1);
    }

    @Test
    void productManagerRejectsDuplicateID() {
        Computer computer = TestProducts.computers(1, 20, 50);
        Electronics electronics = TestProducts.electronics(1, 50, 40);

        manager.addProduct(computer);

        assertThatThrownBy(() -> manager.addProduct(electronics))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product already exists");
    }

    @Test
    void productManagerRemovesProduct() {
        Computer computer = TestProducts.computers(1, 20, 50);

        manager.addProduct(computer);
        manager.removeProduct(1);

        assertThat(manager.findAll().size()).isEqualTo(0);
    }

    @Test
    void productManagerUpdatesProductWithSameID() {
        Computer computer = TestProducts.computers(1, 20, 50);
        Computer computerUpdated = TestProducts.computers(1, 40, 50);

        manager.addProduct(computer);
        manager.update(computerUpdated);

        assertThat(manager.findById(1)).contains(computerUpdated);
        assertThat(computerUpdated.getPrice()).isEqualTo(40.0);
    }

    @Test
    void throwsWhenUpdatingIncorrectID() {
        Computer computerUpdated = TestProducts.computers(3, 40, 50);

        assertThatThrownBy(() -> manager.update(computerUpdated))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product doesn't exist: ");
    }

    @Test
    void throwsWhenRemovingMissingProduct() {
        assertThatThrownBy(() -> manager.removeProduct(1))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product doesn't exist: ");
    }

    @Test
    void filtersByType() {
        Computer computer = TestProducts.computers(1, 20, 50);
        manager.addProduct(computer);

        assertThat(manager.findByType(ProductType.COMPUTER)).containsExactly(computer);
    }
}
