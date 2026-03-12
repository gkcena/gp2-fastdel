package com.example.backend.service.impl;

import com.example.backend.dto.*;
import com.example.backend.model.*;
import com.example.backend.repository.CourierRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VehicleChangeRequestRepository;
import com.example.backend.service.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final CourierRepository courierRepository;
    private final VehicleChangeRequestRepository vehicleChangeRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(
            UserRepository userRepository,
            CourierRepository courierRepository,
            VehicleChangeRequestRepository vehicleChangeRequestRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.courierRepository = courierRepository;
        this.vehicleChangeRequestRepository = vehicleChangeRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.STAFF
        );
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse createAdmin(CreateAdminRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.ADMIN
        );
        user = userRepository.save(user);
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public CourierResponse createCourier(CreateCourierRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.COURIER
        );
        user = userRepository.save(user);

        Courier courier = new Courier(user);
        courier = courierRepository.save(courier);

        return CourierResponse.from(courier);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("User already deleted");
        }

        user.setDeletedAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listStaff() {
        return userRepository.findByRoleAndDeletedAtIsNull(Role.STAFF)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listAdmins() {
        return userRepository.findByRoleAndDeletedAtIsNull(Role.ADMIN)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierResponse> listCouriers() {
        return userRepository.findByRoleAndDeletedAtIsNull(Role.COURIER)
                .stream()
                .map(user -> {
                    Courier courier = courierRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Courier record missing for user: " + user.getEmail()));
                    return CourierResponse.from(courier);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleChangeRequestResponse> listPendingVehicleRequests() {
        return vehicleChangeRequestRepository
                .findAllByStatus(VehicleChangeStatus.PENDING)
                .stream()
                .map(VehicleChangeRequestResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public VehicleChangeRequestResponse approveVehicleRequest(UUID requestId) {
        VehicleChangeRequest request = vehicleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != VehicleChangeStatus.PENDING) {
            throw new IllegalArgumentException("Request is not pending");
        }

        Courier courier = request.getCourier();
        courier.setVehicleType(request.getRequestedVehicleType());
        courier.setLicensePlate(request.getRequestedLicensePlate());
        courierRepository.save(courier);

        request.setStatus(VehicleChangeStatus.APPROVED);
        request = vehicleChangeRequestRepository.save(request);

        return VehicleChangeRequestResponse.from(request);
    }

    @Override
    @Transactional
    public VehicleChangeRequestResponse rejectVehicleRequest(UUID requestId) {
        VehicleChangeRequest request = vehicleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != VehicleChangeStatus.PENDING) {
            throw new IllegalArgumentException("Request is not pending");
        }

        request.setStatus(VehicleChangeStatus.REJECTED);
        request = vehicleChangeRequestRepository.save(request);

        return VehicleChangeRequestResponse.from(request);
    }
}
