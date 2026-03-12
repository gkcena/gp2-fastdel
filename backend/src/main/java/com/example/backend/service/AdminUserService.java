package com.example.backend.service;

import com.example.backend.dto.*;
import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    UserResponse createStaff(CreateStaffRequest request);
    UserResponse createAdmin(CreateAdminRequest request);
    CourierResponse createCourier(CreateCourierRequest request);
    void deleteUser(UUID id);
    List<UserResponse> listStaff();
    List<UserResponse> listAdmins();
    List<CourierResponse> listCouriers();
    List<VehicleChangeRequestResponse> listPendingVehicleRequests();
    VehicleChangeRequestResponse approveVehicleRequest(UUID requestId);
    VehicleChangeRequestResponse rejectVehicleRequest(UUID requestId);
}
