package com.example.backend.service.impl;

import com.example.backend.dto.CourierResponse;
import com.example.backend.dto.SetVehicleRequest;
import com.example.backend.dto.VehicleChangeRequestDto;
import com.example.backend.dto.VehicleChangeRequestResponse;
import com.example.backend.model.Courier;
import com.example.backend.model.VehicleChangeRequest;
import com.example.backend.model.VehicleChangeStatus;
import com.example.backend.repository.CourierRepository;
import com.example.backend.repository.VehicleChangeRequestRepository;
import com.example.backend.service.CourierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final VehicleChangeRequestRepository vehicleChangeRequestRepository;

    public CourierServiceImpl(
            CourierRepository courierRepository,
            VehicleChangeRequestRepository vehicleChangeRequestRepository
    ) {
        this.courierRepository = courierRepository;
        this.vehicleChangeRequestRepository = vehicleChangeRequestRepository;
    }

    @Override
    @Transactional
    public CourierResponse setVehicle(String courierEmail, SetVehicleRequest request) {
        Courier courier = courierRepository.findByUserEmail(courierEmail)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        if (courier.getVehicleType() != null) {
            throw new IllegalArgumentException(
                    "Please contact your administrator to change vehicle information"
            );
        }

        courier.setVehicleType(request.vehicleType());
        courier.setLicensePlate(request.licensePlate());
        courier = courierRepository.save(courier);

        return CourierResponse.from(courier);
    }

    @Override
    @Transactional
    public VehicleChangeRequestResponse requestVehicleChange(
            String courierEmail, VehicleChangeRequestDto request
    ) {
        Courier courier = courierRepository.findByUserEmail(courierEmail)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        boolean hasPending = vehicleChangeRequestRepository
                .existsByCourierIdAndStatus(courier.getId(), VehicleChangeStatus.PENDING);

        if (hasPending) {
            throw new IllegalArgumentException(
                    "You already have a pending vehicle change request"
            );
        }

        VehicleChangeRequest changeRequest = new VehicleChangeRequest(
                courier,
                request.vehicleType(),
                request.licensePlate()
        );
        changeRequest = vehicleChangeRequestRepository.save(changeRequest);

        return VehicleChangeRequestResponse.from(changeRequest);
    }
}
