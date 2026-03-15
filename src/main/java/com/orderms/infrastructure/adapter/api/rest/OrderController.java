package com.orderms.infrastructure.adapter.api.rest;

import com.orderms.application.port.input.OrderUseCase;
import com.orderms.domain.model.OrderStatus;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.application.mapper.OrderMapper;
import jakarta.validation.Valid;
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
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
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

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable String orderId,
                                                @Valid @RequestBody OrderDTO orderDTO) {
        orderDTO.setOrderId(orderId);
        return ResponseEntity.ok(
                orderMapper.toDTO(
                        orderUseCase.updateOrder(orderMapper.toDomain(orderDTO))
                )
        );
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        orderUseCase.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String orderId,
                                                      @RequestParam OrderStatus status) {
        return ResponseEntity.ok(
                orderMapper.toDTO(orderUseCase.updateOrderStatus(orderId, status))
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(
                orderUseCase.getOrdersByCustomerId(customerId).stream()
                        .map(orderMapper::toDTO)
                        .toList()
        );
    }

    @PostMapping("/{orderId}/process")
    public ResponseEntity<OrderDTO> processOrder(@PathVariable String orderId) {
        return orderUseCase.processOrder(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}