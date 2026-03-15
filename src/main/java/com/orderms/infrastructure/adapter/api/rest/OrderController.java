package com.orderms.infrastructure.adapter.api.rest;

import com.orderms.application.port.input.OrderUseCase;
import com.orderms.domain.model.OrderStatus;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.application.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management API")
public class OrderController {
    private final OrderUseCase orderUseCase;
    private final OrderMapper orderMapper;

    @Operation(summary = "Create a new order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(
                orderMapper.toDTO(
                        orderUseCase.createOrder(orderMapper.toDomain(orderDTO))
                )
        );
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @Parameter(description = "ID of the order to retrieve") @PathVariable String orderId) {
        return orderUseCase.getOrder(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all orders", description = "Supports pagination, sorting and optional filtering by status")
    @ApiResponse(responseCode = "200", description = "Paginated list of orders")
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Filter by order status") @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(
                orderUseCase.getAllOrders(pageable, status).map(orderMapper::toDTO)
        );
    }

    @Operation(summary = "Update an existing order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "ID of the order to update") @PathVariable String orderId,
            @Valid @RequestBody OrderDTO orderDTO) {
        orderDTO.setOrderId(orderId);
        return ResponseEntity.ok(
                orderMapper.toDTO(orderUseCase.updateOrder(orderMapper.toDomain(orderDTO)))
        );
    }

    @Operation(summary = "Delete an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "ID of the order to delete") @PathVariable String orderId) {
        orderUseCase.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update the status of an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "ID of the order") @PathVariable String orderId,
            @Parameter(description = "New status to apply") @RequestParam OrderStatus status) {
        return ResponseEntity.ok(
                orderMapper.toDTO(orderUseCase.updateOrderStatus(orderId, status))
        );
    }

    @Operation(summary = "Get all orders for a customer")
    @ApiResponse(responseCode = "200", description = "List of orders for the given customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(
            @Parameter(description = "ID of the customer") @PathVariable String customerId) {
        return ResponseEntity.ok(
                orderUseCase.getOrdersByCustomerId(customerId).stream()
                        .map(orderMapper::toDTO)
                        .toList()
        );
    }

    @Operation(summary = "Process an order", description = "Confirms a CREATED order and moves it to CONFIRMED status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order processed successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/process")
    public ResponseEntity<OrderDTO> processOrder(
            @Parameter(description = "ID of the order to process") @PathVariable String orderId) {
        return orderUseCase.processOrder(orderId)
                .map(order -> ResponseEntity.ok(orderMapper.toDTO(order)))
                .orElse(ResponseEntity.notFound().build());
    }
}