package com.orderms.infrastructure.adapter.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Auto-generated UUID")
    private String orderId;

    @NotEmpty(message = "Customer ID is required")
    private String customerId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> items;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Calculated from items")
    private BigDecimal totalAmount;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Order status")
    private String status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}