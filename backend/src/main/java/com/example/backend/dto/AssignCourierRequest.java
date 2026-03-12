package com.example.backend.dto;

import java.util.UUID;

/**
 * Request DTO for assigning a courier to an order.
 * Used by ADMIN via POST /api/v1/admin/orders/{id}/assign.
 */
public record AssignCourierRequest(
        UUID courierId
) {
}
