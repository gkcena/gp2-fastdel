package com.example.backend.dto;

import com.example.backend.model.Courier;
import com.example.backend.model.VehicleType;

import java.time.Instant;
import java.util.UUID;

public record CourierResponse(
        UUID id,
        String name,
        String email,
        VehicleType vehicleType,
        String licensePlate,
        Instant createdAt
) {
    public static CourierResponse from(Courier courier) {
        return new CourierResponse(
                courier.getId(),
                courier.getUser().getName(),
                courier.getUser().getEmail(),
                courier.getVehicleType(),
                courier.getLicensePlate(),
                courier.getCreatedAt()
        );
    }
}
