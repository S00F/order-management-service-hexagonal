package com.orderms.infrastructure.adapter.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderms.application.mapper.OrderMapper;
import com.orderms.application.port.input.OrderUseCase;
import com.orderms.domain.exception.OrderNotFoundException;
import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderItem;
import com.orderms.domain.model.OrderStatus;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.infrastructure.adapter.api.dto.OrderItemDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderUseCase orderUseCase;

    @MockBean
    private OrderMapper orderMapper;

    private OrderDTO buildValidOrderDTO() {
        return OrderDTO.builder()
                .customerId("cust-1")
                .items(List.of(OrderItemDTO.builder()
                        .productId("p1")
                        .quantity(2)
                        .unitPrice(new BigDecimal("10.00"))
                        .build()))
                .build();
    }

    private Order buildOrder(String id) {
        return Order.builder()
                .orderId(id)
                .customerId("cust-1")
                .status(OrderStatus.CREATED)
                .totalAmount(new BigDecimal("20.00"))
                .items(List.of(OrderItem.builder().productId("p1").quantity(2).unitPrice(new BigDecimal("10.00")).build()))
                .build();
    }

    @Test
    void createOrder_returns200WithCreatedOrder() throws Exception {
        OrderDTO requestDTO = buildValidOrderDTO();
        Order domain = buildOrder("order-1");
        OrderDTO responseDTO = buildValidOrderDTO();
        responseDTO.setOrderId("order-1");

        when(orderMapper.toDomain(any(OrderDTO.class))).thenReturn(domain);
        when(orderUseCase.createOrder(any())).thenReturn(domain);
        when(orderMapper.toDTO(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-1"));
    }

    @Test
    void createOrder_returns400WhenCustomerIdMissing() throws Exception {
        OrderDTO invalid = OrderDTO.builder()
                .items(List.of(OrderItemDTO.builder().productId("p1").quantity(1).unitPrice(BigDecimal.TEN).build()))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_returns200WhenFound() throws Exception {
        Order domain = buildOrder("order-1");
        OrderDTO dto = buildValidOrderDTO();
        dto.setOrderId("order-1");

        when(orderUseCase.getOrder("order-1")).thenReturn(Optional.of(domain));
        when(orderMapper.toDTO(domain)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/orders/order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-1"));
    }

    @Test
    void getOrder_returns404WhenNotFound() throws Exception {
        when(orderUseCase.getOrder("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrders_returnsPaginatedOrders() throws Exception {
        Order domain = buildOrder("order-1");
        OrderDTO dto = buildValidOrderDTO();
        dto.setOrderId("order-1");

        when(orderUseCase.getAllOrders(any(Pageable.class), eq(null)))
                .thenReturn(new PageImpl<>(List.of(domain)));
        when(orderMapper.toDTO(domain)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/orders?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderId").value("order-1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllOrders_filtersByStatus() throws Exception {
        Order domain = buildOrder("order-1");
        OrderDTO dto = buildValidOrderDTO();
        dto.setStatus("CREATED");

        when(orderUseCase.getAllOrders(any(Pageable.class), eq(OrderStatus.CREATED)))
                .thenReturn(new PageImpl<>(List.of(domain)));
        when(orderMapper.toDTO(domain)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/orders?status=CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @Test
    void deleteOrder_returns204OnSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/order-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_returns404WhenNotFound() throws Exception {
        doThrow(new OrderNotFoundException("Order not found with id: missing"))
                .when(orderUseCase).deleteOrder("missing");

        mockMvc.perform(delete("/api/v1/orders/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateOrderStatus_returns200WithUpdatedOrder() throws Exception {
        Order domain = buildOrder("order-1");
        domain.setStatus(OrderStatus.CONFIRMED);
        OrderDTO dto = buildValidOrderDTO();
        dto.setStatus("CONFIRMED");

        when(orderUseCase.updateOrderStatus(eq("order-1"), eq(OrderStatus.CONFIRMED))).thenReturn(domain);
        when(orderMapper.toDTO(domain)).thenReturn(dto);

        mockMvc.perform(patch("/api/v1/orders/order-1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}