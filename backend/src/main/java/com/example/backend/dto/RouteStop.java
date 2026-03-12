package com.example.backend.dto;

import java.util.UUID;

public record RouteStop(
        UUID orderId,
        String barcode,
        String customerName,
        String deliveryAddress,
        int optimizedOrder
) {
}
