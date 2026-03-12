package com.example.backend.repository;

import com.example.backend.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourierRepository extends JpaRepository<Courier, UUID> {

    Optional<Courier> findByUserId(UUID userId);

    Optional<Courier> findByUserEmail(String email);
}
