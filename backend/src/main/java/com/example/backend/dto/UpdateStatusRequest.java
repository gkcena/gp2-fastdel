package com.example.backend.dto;

import com.example.backend.model.OrderStatus;

/**
 * Request DTO for updating an order's delivery status.
 * Used by ADMIN (PUT /api/v1/admin/orders/{id}/status)
 * and COURIER (PUT /api/v1/courier/orders/{id}/status).
 */
public record UpdateStatusRequest(
        OrderStatus status
) {
}
