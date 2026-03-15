package com.orderms.domain.service;

import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderDomainService {


    public Order buildOrder(Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(calculateOrderTotal(order));
        return order;
    }

    public Order updateOrder(Order order) {

        order.setUpdatedAt(LocalDateTime.now());
        order.setTotalAmount(calculateOrderTotal(order));
        return order;
    }

    public Order updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public Order processOrder(Order order) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(LocalDateTime.now());
            return order;
    }

    public BigDecimal calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}