package com.example.backend.dto;

import com.example.backend.model.Order;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for order data.
 * Never exposes the Order entity directly (rules.md § 4.1).
 */
public record OrderResponse(
        UUID id,
        String barcode,
        String customerName,
        String customerPhone,
        String deliveryAddress,
        String status,
        String assignedCourierName,
        String createdByName,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponse from(Order order) {
        String courierName = null;
        if (order.getAssignedCourier() != null) {
            courierName = order.getAssignedCourier().getUser().getName();
        }

        return new OrderResponse(
                order.getId(),
                order.getBarcode(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getDeliveryAddress(),
                order.getStatus().name(),
                courierName,
                order.getCreatedBy().getName(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
