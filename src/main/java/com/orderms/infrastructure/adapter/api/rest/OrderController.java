package com.orderms.infrastructure.adapter.api.rest;

import com.orderms.application.port.input.OrderUseCase;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.application.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderUseCase orderUseCase;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(
                orderMapper.toDTO(
                        orderUseCase.createOrder(
                                orderMapper.toDomain(orderDTO)
                        )
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String orderId) {
        return orderUseCase.getOrder(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(
                orderUseCase.getAllOrders().stream()
                        .map(orderMapper::toDTO)
                        .toList()
        );
    }
}