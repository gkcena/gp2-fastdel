package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Vehicle change request entity — maps to "vehicle_change_requests" table.
 * A courier can have at most 1 PENDING request at a time.
 */
@Entity
@Table(name = "vehicle_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_vehicle_type", nullable = false)
    private VehicleType requestedVehicleType;

    @Column(name = "requested_license_plate", nullable = false)
    private String requestedLicensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleChangeStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public VehicleChangeRequest(Courier courier, VehicleType requestedVehicleType,
                                 String requestedLicensePlate) {
        this.courier = courier;
        this.requestedVehicleType = requestedVehicleType;
        this.requestedLicensePlate = requestedLicensePlate;
        this.status = VehicleChangeStatus.PENDING;
    }
}
