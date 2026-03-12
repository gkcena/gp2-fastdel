package com.example.backend.dto;

import com.example.backend.model.VehicleChangeRequest;
import com.example.backend.model.VehicleType;

import java.time.Instant;
import java.util.UUID;

public record VehicleChangeRequestResponse(
        UUID id,
        String courierName,
        String courierEmail,
        VehicleType requestedVehicleType,
        String requestedLicensePlate,
        String status,
        Instant createdAt
) {
    public static VehicleChangeRequestResponse from(VehicleChangeRequest req) {
        return new VehicleChangeRequestResponse(
                req.getId(),
                req.getCourier().getUser().getName(),
                req.getCourier().getUser().getEmail(),
                req.getRequestedVehicleType(),
                req.getRequestedLicensePlate(),
                req.getStatus().name(),
                req.getCreatedAt()
        );
    }
}
