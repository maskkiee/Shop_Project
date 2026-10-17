package com.example.shop.TASK1;

import com.example.shop.model.*;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductsTests {
    @Test
    void createsComputerCorrectly() {
        Computer computer = TestProducts.computers(1, 20, 50);

        assertThat(computer.getId()).isEqualTo(1);
        assertThat(computer.getPrice()).isEqualTo(20.0);
        assertThat(computer.getStock()).isEqualTo(50);
    }

    @Test
    void createsCustomerCorrectly() {
        Customer customer = TestProducts.customers();

        assertThat(customer.name()).isEqualTo("Anna Nowak");
        assertThat(customer.email()).isEqualTo("anowak@gmail.com");
    }

    @Test
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> TestProducts.computers(1, -20, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cena nie może być ujemna");
    }

    @Test
    void rejectsNegativeStock() {
        assertThatThrownBy(() -> TestProducts.computers(1, 20, -50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ilość nie może być ujemna");
    }

    @Test
    void decreasesStockByRequest() {
        Electronics headphones = TestProducts.electronics(1, 20, 50);

        headphones.reduceStock(1);

        assertThat(headphones.getStock()).isEqualTo(49);
    }

    @Test
    void throwsExceptionAtStockBelowZero() {
        Electronics headphones = TestProducts.electronics(1, 20, 5);

        assertThatThrownBy(() -> headphones.reduceStock(6))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("Brak ilość na magazynie");
    }

    @Test
    void rejectsNonPositiveRequestForStock() {
        Electronics headphones = TestProducts.electronics(1, 20, 5);

        assertThatThrownBy(() -> headphones.reduceStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ilość musi być dodatnia");
        assertThatThrownBy(() -> headphones.reduceStock(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ilość musi być dodatnia");
    }

    @Test
    void computerSpecProceeds() {
        ComputerSpec compSpecification = new ComputerSpec("Intel i7", 32, 1024);

        assertThat(compSpecification.cpu()).isEqualTo("Intel i7");
        assertThat(compSpecification.ramGB()).isEqualTo(32);
        assertThat(compSpecification.storageGB()).isEqualTo(1024);
    }

    @Test
    void throwsAtComputerSpecCPUNull() {
        assertThatThrownBy(() -> new ComputerSpec(null, 32, 1024))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPU nie może być pusty ani null");
    }

    @Test
    void throwsAtComputerSpecRamNegative() {
        assertThatThrownBy(() -> new ComputerSpec
                ("Czarny", -5000, 1024))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ram GB nie może być mniejszy niż 1GB");
    }

    @Test
    void throwsAtComputerSpecStorageNegative() {
        assertThatThrownBy(() -> new ComputerSpec
                ("Czarny", 5000, -1024))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Storage GB nie może być mniejszy niż 1GB");
    }

    @Test
    void configureOnComputerSavesCorrectValues() {
        Computer computer = TestProducts.computers(1, 20, 50);
        ComputerSpec spec = new ComputerSpec ("Czarny", 5000, 1024);

        computer.configure(spec);

        assertThat(computer.getSpec()).contains(spec);
    }

    @Test
    void configureOnComputerThrowsAtNull() {
        Computer computer = TestProducts.computers(1, 20, 50);

        assertThatThrownBy(() -> computer.configure(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Specyfikacja nie może być pusta");
    }
    @Test
    void createsSmartphoneCorrectly() {
        Smartphone smartphone = TestProducts.smartphones(1, 20, 50);

        assertThat(smartphone.getId()).isEqualTo(1);
        assertThat(smartphone.getPrice()).isEqualTo(20.0);
        assertThat(smartphone.getStock()).isEqualTo(50);
    }

    @Test
    void smartphoneSpecProceeds() {
        SmartphoneSpec smartphoneSpecification = new SmartphoneSpec
                ("Czarny", 5000, List.of("Etui"));

        assertThat(smartphoneSpecification.accesories()).containsExactly("Etui");
        assertThat(smartphoneSpecification.color()).isEqualTo("Czarny");
        assertThat(smartphoneSpecification.batteryCapacity()).isEqualTo(5000);
    }

    @Test
    void throwsAtSmartphoneSpecColorNull() {
        assertThatThrownBy(() -> new SmartphoneSpec
                (null, 5000, List.of("Etui")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kolor nie może być null ani pusty");
    }

    @Test
    void throwsAtSmartphoneSpecListNull() {
        assertThatThrownBy(() -> new SmartphoneSpec
                ("Czarny", 5000, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Accessories nie może być null ani pusty");
    }

    @Test
    void throwsAtSmartphoneSpecCapacityNegative() {
        assertThatThrownBy(() -> new SmartphoneSpec
                ("Czarny", -5000, List.of("Etui")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pojemność baterii jest poza zakresem: ");
    }

    @Test
    void configureOnSmartphoneSavesCorrectValues() {
        Smartphone smartphone = TestProducts.smartphones(1, 20, 50);
        SmartphoneSpec spec = new SmartphoneSpec("Czarny", 5000, List.of("Etui"));

        smartphone.configure(spec);

        assertThat(smartphone.getSpec()).contains(spec);
    }

    @Test
    void configureOnSmartphoneThrowsAtNull() {
        Smartphone smartphone = TestProducts.smartphones(1, 20, 50);

        assertThatThrownBy(() -> smartphone.configure(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Specyfikacja nie może być pusta");
    }
}