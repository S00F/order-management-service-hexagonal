package com.orderms.domain.service;

import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderItem;
import com.orderms.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDomainServiceTest {

    private OrderDomainService orderDomainService;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainService();
    }

    @Test
    void buildOrder_setsIdStatusTimestampAndTotal() {
        Order order = Order.builder()
                .customerId("cust-1")
                .items(List.of(
                        OrderItem.builder().productId("p1").quantity(2).unitPrice(new BigDecimal("10.00")).build(),
                        OrderItem.builder().productId("p2").quantity(1).unitPrice(new BigDecimal("5.00")).build()
                ))
                .build();

        Order result = orderDomainService.buildOrder(order);

        assertThat(result.getOrderId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void updateOrder_recalculatesTotalAndSetsUpdatedAt() {
        Order order = Order.builder()
                .orderId("existing-id")
                .customerId("cust-1")
                .items(List.of(
                        OrderItem.builder().productId("p1").quantity(3).unitPrice(new BigDecimal("10.00")).build()
                ))
                .build();

        Order result = orderDomainService.updateOrder(order);

        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateOrderStatus_setsStatusAndUpdatedAt() {
        Order order = Order.builder().orderId("id").status(OrderStatus.CREATED).build();

        Order result = orderDomainService.updateOrderStatus(order, OrderStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void processOrder_setsConfirmedStatus() {
        Order order = Order.builder().orderId("id").status(OrderStatus.CREATED).build();

        Order result = orderDomainService.processOrder(order);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void calculateOrderTotal_sumsItemSubtotals() {
        Order order = Order.builder()
                .items(List.of(
                        OrderItem.builder().productId("p1").quantity(2).unitPrice(new BigDecimal("15.50")).build(),
                        OrderItem.builder().productId("p2").quantity(4).unitPrice(new BigDecimal("5.00")).build()
                ))
                .build();

        BigDecimal total = orderDomainService.calculateOrderTotal(order);

        assertThat(total).isEqualByComparingTo(new BigDecimal("51.00"));
    }
}