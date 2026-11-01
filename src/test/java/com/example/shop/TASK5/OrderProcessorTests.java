package com.example.shop.TASK5;

import com.example.shop.cart.Cart;
import com.example.shop.exceptions.OrderProcessingException;
import com.example.shop.exceptions.OutOfStockException;
import com.example.shop.model.Computer;
import com.example.shop.order.InMemoryOrderRepository;
import com.example.shop.order.InvoiceGenerator;
import com.example.shop.order.Order;
import com.example.shop.order.OrderProcessor;
import com.example.shop.promo.PromoService;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import static com.example.shop.cart.Cart.addToCart;
import static com.example.shop.support.TestProducts.customers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderProcessorTests {

    private final PromoService promos = TestProducts.samplePromos();
    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final OrderProcessor processor =
            new OrderProcessor(new InvoiceGenerator(), repository, promos);

    @Test
    public void createsOrderAndInvoice() {
        Computer computer = TestProducts.computers(1, 20, 50);
        Cart cart = addToCart(computer, 2);

        Order order = processor.process(cart, customers());

        assertThat(order.getId()).startsWith("ORD-");
        assertThat(order.getTotal()).isEqualTo(40.0);
        assertThat(order.getCreatedAt().getOffset())
                .isEqualTo(Order.SHOP_ZONE.getRules().getOffset(order.getCreatedAt().toInstant()));
        assertThat(computer.getStock()).isEqualTo(48);
        assertThat(cart.isEmpty()).isTrue();
        assertThat(repository.findInvoice(order.getId()).orElse("")).contains("Invoice: " + order.getId());
    }

    @Test
    public void throwsAtEmptyCart() {
        assertThatThrownBy(() -> processor.process(new Cart(), customers()))
                .isInstanceOf(OrderProcessingException.class)
        .hasMessageContaining("Cart is empty");
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void throwsAtQuantityHigherThanStock(){
        Computer computer = TestProducts.computers(1, 20, 1);
        Cart cart = addToCart(computer, 2);

        assertThatThrownBy(() -> processor.process(cart, customers()))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("Out of stock");

        assertThat(computer.getStock()).isEqualTo(1);
        assertThat(repository.findAll()).isEmpty();
    }
}
