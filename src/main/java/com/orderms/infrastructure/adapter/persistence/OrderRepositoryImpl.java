package com.orderms.infrastructure.adapter.persistence;

import com.orderms.application.port.output.OrderRepository;
import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderStatus;
import com.orderms.infrastructure.adapter.persistence.repository.JpaOrderRepository;
import com.orderms.application.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        }
        var savedEntity = jpaOrderRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return jpaOrderRepository.findById(orderId)
                .map(orderMapper::toDomain);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return jpaOrderRepository.findAll(pageable)
                .map(orderMapper::toDomain);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaOrderRepository.findByStatus(status, pageable)
                .map(orderMapper::toDomain);
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