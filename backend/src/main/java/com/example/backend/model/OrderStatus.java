package com.example.backend.model;

/**
 * Order delivery status — tracks the lifecycle of a delivery order.
 *
 * Valid transitions:
 *   PENDING → ASSIGNED (via assignCourier)
 *   ASSIGNED → IN_TRANSIT
 *   IN_TRANSIT → DELIVERED or FAILED
 */
public enum OrderStatus {
    PENDING,
    ASSIGNED,
    IN_TRANSIT,
    DELIVERED,
    FAILED
}
