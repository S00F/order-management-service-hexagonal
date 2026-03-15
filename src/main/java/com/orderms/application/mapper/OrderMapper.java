package com.orderms.application.mapper;

import com.orderms.domain.model.OrderItem;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.infrastructure.adapter.persistence.entity.OrderEntity;
import com.orderms.infrastructure.adapter.persistence.entity.OrderItemEntity;
import com.orderms.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    Order toDomain(OrderDTO orderDTO);

    @Mapping(source = "id", target = "orderId")
    Order toDomain(OrderEntity orderEntity);

    @Mapping(source = "orderId", target = "id")
    OrderEntity toEntity(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItem orderItem);
}