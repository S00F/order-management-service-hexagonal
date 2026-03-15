package com.orderms.application.service;

import com.orderms.application.port.input.OrderUseCase;
import com.orderms.application.port.output.OrderRepository;
import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderStatus;
import com.orderms.domain.exception.OrderNotFoundException;
import com.orderms.domain.service.OrderDomainService;
import com.orderms.infrastructure.validator.OrderValidator;
import com.orderms.infrastructure.validator.ValidationResult;
import com.orderms.infrastructure.validator.OrderValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OrderValidator orderValidator;

    public OrderService(OrderRepository orderRepository, OrderDomainService orderDomainService, OrderValidator orderValidator) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.orderValidator = orderValidator;
    }

    @Override
    public Order createOrder(Order order) {
        ValidationResult validationResult = orderValidator.validate(order);
        if (!validationResult.isValid()) {
            throw new OrderValidationException(validationResult.getErrors());
        }
        return orderRepository.save(orderDomainService.buildOrder(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable);
        }
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order updateOrder(Order order) {
        ValidationResult validationResult = orderValidator.validate(order);
        if (!validationResult.isValid()) {
            throw new OrderValidationException(validationResult.getErrors());
        }
        if (!orderRepository.existsById(order.getOrderId())) {
            throw new OrderNotFoundException("Order not found with id: " + order.getOrderId());
        }
        return orderRepository.save(orderDomainService.updateOrder(order));
    }

    @Override
    public void deleteOrder(String orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return orderRepository.save(orderDomainService.updateOrderStatus(order, status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Order> processOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    ValidationResult validationResult = orderValidator.validate(order);
                    if (validationResult.isValid()) {
                        return orderRepository.save(orderDomainService.processOrder(order));
                    }
                    throw new OrderValidationException(validationResult.getErrors());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateOrder(Order order) {
        return orderValidator.validate(order).isValid();
    }
}