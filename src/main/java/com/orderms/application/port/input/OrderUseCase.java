package com.orderms.application.port.input;

import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderUseCase {
    Order createOrder(Order order);
    Optional<Order> getOrder(String orderId);
    Page<Order> getAllOrders(Pageable pageable, OrderStatus status);
    Order updateOrder(Order order);
    void deleteOrder(String orderId);
    Order updateOrderStatus(String orderId, OrderStatus status);
    List<Order> getOrdersByCustomerId(String customerId);
    Optional<Order> processOrder(String orderId);
    boolean validateOrder(Order order);
}