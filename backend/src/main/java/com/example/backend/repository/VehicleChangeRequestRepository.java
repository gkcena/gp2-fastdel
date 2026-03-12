package com.example.backend.repository;

import com.example.backend.model.VehicleChangeRequest;
import com.example.backend.model.VehicleChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleChangeRequestRepository extends JpaRepository<VehicleChangeRequest, UUID> {

    boolean existsByCourierIdAndStatus(UUID courierId, VehicleChangeStatus status);

    List<VehicleChangeRequest> findAllByStatus(VehicleChangeStatus status);
}
