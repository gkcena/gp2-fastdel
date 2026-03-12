package com.example.backend.controller;

import com.example.backend.dto.CourierResponse;
import com.example.backend.dto.SetVehicleRequest;
import com.example.backend.dto.VehicleChangeRequestDto;
import com.example.backend.dto.VehicleChangeRequestResponse;
import com.example.backend.service.CourierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Courier-facing controller — /api/v1/courier
 * All endpoints require COURIER role (enforced by SecurityConfig).
 *
 * The courier's email is extracted from the JWT via Authentication principal.
 */
@RestController
@RequestMapping("/api/v1/courier")
public class CourierController {

    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    /**
     * POST /api/v1/courier/vehicle — first-time vehicle setup.
     */
    @PostMapping("/vehicle")
    public ResponseEntity<CourierResponse> setVehicle(
            @RequestBody SetVehicleRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CourierResponse response = courierService.setVehicle(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/courier/vehicle/change-request — request vehicle change.
     */
    @PostMapping("/vehicle/change-request")
    public ResponseEntity<VehicleChangeRequestResponse> requestVehicleChange(
            @RequestBody VehicleChangeRequestDto request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        VehicleChangeRequestResponse response = courierService.requestVehicleChange(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
