package com.orderms.infrastructure.validator;

import com.orderms.domain.model.Order;
import com.orderms.domain.model.OrderItem;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderValidator {

    public ValidationResult validate(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            return new ValidationResult(false, List.of("Order cannot be null"));
        }

        validateCustomerId(order, errors);
        validateItems(order, errors);

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private void validateCustomerId(Order order, List<String> errors) {
        if (order.getCustomerId() == null || order.getCustomerId().trim().isEmpty()) {
            errors.add("Customer ID is required");
        }
    }

    private void validateItems(Order order, List<String> errors) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            errors.add("Order must contain at least one item");
            return;
        }

        for (OrderItem item : order.getItems()) {
            validateOrderItem(item, errors);
        }
    }

    private void validateOrderItem(OrderItem item, List<String> errors) {
        if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
            errors.add("Product ID is required for all items");
        }

        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            errors.add("Quantity must be greater than zero");
        }

        if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Unit price cannot be negative");
        }
    }

}