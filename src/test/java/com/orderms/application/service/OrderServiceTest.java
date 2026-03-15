package com.orderms.application.service;

import com.orderms.application.port.output.OrderRepository;
import com.orderms.domain.exception.OrderNotFoundException;
import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderItem;
import com.orderms.domain.model.OrderStatus;
import com.orderms.domain.service.OrderDomainService;
import com.orderms.infrastructure.validator.OrderValidationException;
import com.orderms.infrastructure.validator.OrderValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderDomainService orderDomainService;
    private OrderValidator orderValidator;
    private OrderService orderService;

    private Order validOrder;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainService();
        orderValidator = new OrderValidator();
        orderService = new OrderService(orderRepository, orderDomainService, orderValidator);

        validOrder = Order.builder()
                .customerId("cust-1")
                .items(List.of(
                        OrderItem.builder().productId("p1").quantity(2).unitPrice(new BigDecimal("10.00")).build()
                ))
                .build();
    }

    @Test
    void createOrder_savesOrderWithGeneratedId() {
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(validOrder);

        assertThat(result.getOrderId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        verify(orderRepository).save(any());
    }

    @Test
    void createOrder_throwsWhenCustomerIdMissing() {
        Order invalid = Order.builder()
                .items(List.of(OrderItem.builder().productId("p1").quantity(1).unitPrice(BigDecimal.TEN).build()))
                .build();

        assertThatThrownBy(() -> orderService.createOrder(invalid))
                .isInstanceOf(OrderValidationException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_throwsWhenItemsEmpty() {
        Order invalid = Order.builder().customerId("cust-1").items(List.of()).build();

        assertThatThrownBy(() -> orderService.createOrder(invalid))
                .isInstanceOf(OrderValidationException.class);
    }

    @Test
    void getOrder_returnsOrderWhenFound() {
        Order saved = Order.builder().orderId("order-1").customerId("cust-1").build();
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(saved));

        Optional<Order> result = orderService.getOrder("order-1");

        assertThat(result).isPresent();
        assertThat(result.get().getOrderId()).isEqualTo("order-1");
    }

    @Test
    void getOrder_returnsEmptyWhenNotFound() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThat(orderService.getOrder("missing")).isEmpty();
    }

    @Test
    void deleteOrder_throwsWhenNotFound() {
        when(orderRepository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> orderService.deleteOrder("missing"))
                .isInstanceOf(OrderNotFoundException.class);

        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void deleteOrder_deletesWhenExists() {
        when(orderRepository.existsById("order-1")).thenReturn(true);

        orderService.deleteOrder("order-1");

        verify(orderRepository).deleteById("order-1");
    }

    @Test
    void updateOrderStatus_throwsWhenNotFound() {
        when(orderRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus("missing", OrderStatus.CONFIRMED))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateOrderStatus_updatesStatusWhenFound() {
        Order existing = Order.builder().orderId("order-1").status(OrderStatus.CREATED).customerId("cust-1").build();
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(existing));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.updateOrderStatus("order-1", OrderStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}