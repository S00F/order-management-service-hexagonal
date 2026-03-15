package com.orderms.infrastructure.adapter.persistence.repository;

import com.orderms.domain.model.OrderStatus;
import com.orderms.infrastructure.adapter.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);
}