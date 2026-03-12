package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin user management controller — /api/v1/admin
 * All endpoints require ADMIN role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to AdminUserService
 *  • DTO-only request/response
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // ---- Admin Management ----

    @PostMapping("/admins")
    public ResponseEntity<UserResponse> createAdmin(@RequestBody CreateAdminRequest request) {
        UserResponse response = adminUserService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserResponse>> listAdmins() {
        return ResponseEntity.ok(adminUserService.listAdmins());
    }

    // ---- Staff Management ----

    @PostMapping("/staff")
    public ResponseEntity<UserResponse> createStaff(@RequestBody CreateStaffRequest request) {
        UserResponse response = adminUserService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserResponse>> listStaff() {
        return ResponseEntity.ok(adminUserService.listStaff());
    }

    // ---- Courier Management ----

    @PostMapping("/couriers")
    public ResponseEntity<CourierResponse> createCourier(@RequestBody CreateCourierRequest request) {
        CourierResponse response = adminUserService.createCourier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/couriers")
    public ResponseEntity<List<CourierResponse>> listCouriers() {
        return ResponseEntity.ok(adminUserService.listCouriers());
    }

    // ---- User Deletion (soft delete) ----

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Vehicle Change Requests ----

    @GetMapping("/vehicle-requests")
    public ResponseEntity<List<VehicleChangeRequestResponse>> listPendingVehicleRequests() {
        return ResponseEntity.ok(adminUserService.listPendingVehicleRequests());
    }

    @PostMapping("/vehicle-requests/{id}/approve")
    public ResponseEntity<VehicleChangeRequestResponse> approveVehicleRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(adminUserService.approveVehicleRequest(id));
    }

    @PostMapping("/vehicle-requests/{id}/reject")
    public ResponseEntity<VehicleChangeRequestResponse> rejectVehicleRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(adminUserService.rejectVehicleRequest(id));
    }
}
