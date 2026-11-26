package com.example.shop.TASK12;

import com.example.shop.cart.Cart;
import com.example.shop.model.Computer;
import com.example.shop.persistence.InMemoryOrderRepository;
import com.example.shop.order.InvoiceGenerator;
import com.example.shop.order.Order;
import com.example.shop.order.OrderProcessor;
import com.example.shop.promo.PromoService;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.shop.support.TestProducts.addToCart;
import static com.example.shop.support.TestProducts.customers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PromoTests {

    private final PromoService promos = TestProducts.samplePromos();
    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final OrderProcessor processor =
            new OrderProcessor(new InvoiceGenerator(), repository, promos);

    private static final BigDecimal EXPECTED_TOTAL_WITH_PROMO = new BigDecimal("36.00");

    @Test
    public void appliesPromoCorrectly() {
        Computer computer = TestProducts.computers(1, BigDecimal.valueOf(20), 50);
        Cart cart = addToCart(computer, 2);

        Order order = processor.process(cart, customers(), "TEST10");

        assertThat(order.getTotal()).isEqualByComparingTo(EXPECTED_TOTAL_WITH_PROMO);
    }

    @Test
    public void throwsAtBlankCodeRegister() {
        assertThatThrownBy(() -> promos.register(" ", BigDecimal.valueOf(0.2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Code cannot be blank");
    }
}
