package com.orderms.application.mapper;

import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.infrastructure.adapter.persistence.entity.OrderEntity;
import com.orderms.domain.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    Order toDomain(OrderDTO orderDTO);
    Order toDomain(OrderEntity orderEntity);
    OrderEntity toEntity(Order order);
}