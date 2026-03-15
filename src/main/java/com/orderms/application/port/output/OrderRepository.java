package com.orderms.application.port.output;

import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String orderId);
    Page<Order> findAll(Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    void deleteById(String orderId);
    boolean existsById(String orderId);
    List<Order> findByCustomerId(String customerId);
}