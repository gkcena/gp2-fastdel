package com.example.backend.repository;

import com.example.backend.model.Order;
import com.example.backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository layer — only JPA/database operations, no business logic.
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findAllByAssignedCourierId(UUID courierId);

    Optional<Order> findByBarcode(String barcode);
}
