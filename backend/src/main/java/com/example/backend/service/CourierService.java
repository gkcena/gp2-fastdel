package com.example.backend.service;

import com.example.backend.dto.CourierResponse;
import com.example.backend.dto.SetVehicleRequest;
import com.example.backend.dto.VehicleChangeRequestDto;
import com.example.backend.dto.VehicleChangeRequestResponse;

public interface CourierService {
    CourierResponse setVehicle(String courierEmail, SetVehicleRequest request);
    VehicleChangeRequestResponse requestVehicleChange(String courierEmail, VehicleChangeRequestDto request);
}
