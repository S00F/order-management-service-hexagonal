package com.orderms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderms.infrastructure.adapter.api.dto.OrderDTO;
import com.orderms.infrastructure.adapter.api.dto.OrderItemDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO buildOrderRequest() {
        return OrderDTO.builder()
                .customerId("cust-integration")
                .items(List.of(
                        OrderItemDTO.builder()
                                .productId("prod-1")
                                .quantity(3)
                                .unitPrice(new BigDecimal("25.00"))
                                .build()
                ))
                .build();
    }

    @Test
    void createOrder_persistsAndReturnsOrderWithGeneratedId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(75.00))
                .andReturn();

        String orderId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("orderId").asText();
        assertThat(orderId).isNotBlank();
    }

    @Test
    void getOrder_returnsCreatedOrder() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andExpect(status().isOk())
                .andReturn();

        String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("orderId").asText();

        mockMvc.perform(get("/api/v1/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.customerId").value("cust-integration"));
    }

    @Test
    void getAllOrders_returnsPaginatedResults() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildOrderRequest())));

        mockMvc.perform(get("/api/v1/orders?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    void getAllOrders_filtersByStatus() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildOrderRequest())));

        mockMvc.perform(get("/api/v1/orders?status=CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.status == 'CREATED')]").exists());
    }

    @Test
    void updateOrderStatus_changesStatus() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andReturn();

        String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("orderId").asText();

        mockMvc.perform(patch("/api/v1/orders/" + orderId + "/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void deleteOrder_removesOrder() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildOrderRequest())))
                .andReturn();

        String orderId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("orderId").asText();

        mockMvc.perform(delete("/api/v1/orders/" + orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/orders/" + orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrder_returns404ForNonExistentOrder() throws Exception {
        mockMvc.perform(get("/api/v1/orders/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_returns400WhenItemsEmpty() throws Exception {
        OrderDTO invalid = OrderDTO.builder()
                .customerId("cust-1")
                .items(List.of())
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}