package com.example.shop.TASK11;

import com.example.shop.cart.Cart;
import com.example.shop.model.Customer;
import com.example.shop.order.InMemoryOrderRepository;
import com.example.shop.order.InvoiceGenerator;
import com.example.shop.order.OrderProcessor;
import com.example.shop.promo.PromoService;
import com.example.shop.support.TestProducts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static com.example.shop.cart.Cart.addToCart;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderProcessorAsyncTest {

    private static final int ORDER_COUNT = 100;

    private final PromoService promos = TestProducts.samplePromos();
    private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
    private final OrderProcessor processor = new OrderProcessor(new InvoiceGenerator(), repository, promos);

    @Test
    public void asyncProcessingIsFasterThanSync() {
        List<Cart> syncCarts = generateCarts();
        List<Customer> syncCustomers = generateCustomers();
        List<Cart> asyncCarts = generateCarts();
        List<Customer> asyncCustomers = generateCustomers();

        long syncStart = System.nanoTime();
        for (int i = 0; i < ORDER_COUNT; i++) {
            processor.process(syncCarts.get(i), syncCustomers.get(i));
        }
        long syncMs = (System.nanoTime() - syncStart) / 1_000_000;
        int syncSaved = repository.findAll().size();

        long asyncStart = System.nanoTime();
        processor.processAsync(asyncCarts, asyncCustomers);
        long asyncMs = (System.nanoTime() - asyncStart) / 1_000_000;
        int asyncSaved = repository.findAll().size() - syncSaved;

        assertThat(syncSaved).isEqualTo(ORDER_COUNT);
        assertThat(asyncSaved).isEqualTo(ORDER_COUNT);
        assertThat(asyncMs).isLessThan(syncMs);
    }

    private List<Cart> generateCarts() {
        return IntStream.range(0, OrderProcessorAsyncTest.ORDER_COUNT)
                .mapToObj(i -> addToCart(TestProducts.electronics(i + 1, 10.0, 5), 1))
                .toList();
    }

    private List<Customer> generateCustomers() {
        return IntStream.range(0, OrderProcessorAsyncTest.ORDER_COUNT)
                .mapToObj(i -> TestProducts.customers())
                .toList();
    }
}