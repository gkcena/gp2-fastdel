package com.example.backend.dto;

/**
 * Request DTO for creating a new delivery order.
 * Used by STAFF via POST /api/v1/staff/orders.
 */
public record CreateOrderRequest(
        String customerName,
        String customerPhone,
        String deliveryAddress
) {
}
