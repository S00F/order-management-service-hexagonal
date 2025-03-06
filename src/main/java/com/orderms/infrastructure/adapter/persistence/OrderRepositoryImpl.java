package com.orderms.infrastructure.adapter.persistence;

import com.orderms.application.port.output.OrderRepository;
import com.orderms.domain.model.Order;
import com.orderms.infrastructure.adapter.persistence.repository.JpaOrderRepository;
import com.orderms.application.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        var orderEntity = orderMapper.toEntity(order);
        var savedEntity = jpaOrderRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return jpaOrderRepository.findById(orderId)
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll().stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String orderId) {
        jpaOrderRepository.deleteById(orderId);
    }

    @Override
    public boolean existsById(String orderId) {
        return jpaOrderRepository.existsById(orderId);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return jpaOrderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }
}