package com.example.backend.dto;

import com.example.backend.model.VehicleType;

public record VehicleChangeRequestDto(
        VehicleType vehicleType,
        String licensePlate
) {
}
