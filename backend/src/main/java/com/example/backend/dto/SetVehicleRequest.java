package com.example.backend.dto;

import com.example.backend.model.VehicleType;

public record SetVehicleRequest(
        VehicleType vehicleType,
        String licensePlate
) {
}
