package com.example.backend.controller;

import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.RouteResponse;
import com.example.backend.dto.UpdateStatusRequest;
import com.example.backend.model.Courier;
import com.example.backend.repository.CourierRepository;
import com.example.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Courier order management controller — /api/v1/courier/orders
 * All endpoints require COURIER role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to OrderService
 *  • DTO-only request/response
 *  • Couriers can transition: ASSIGNED → IN_TRANSIT, IN_TRANSIT → DELIVERED or FAILED
 */
@RestController
@RequestMapping("/api/v1/courier/orders")
public class CourierOrderController {

    private final OrderService orderService;
    private final CourierRepository courierRepository;

    public CourierOrderController(OrderService orderService, CourierRepository courierRepository) {
        this.orderService = orderService;
        this.courierRepository = courierRepository;
    }

    /**
     * GET /api/v1/courier/orders — list orders assigned to the current courier.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listMyOrders() {
        Courier courier = getCurrentCourier();
        return ResponseEntity.ok(orderService.listCourierOrders(courier.getId()));
    }

    /**
     * GET /api/v1/courier/orders/route — returns RouteResponse for the logged-in courier.
     */
    @GetMapping("/route")
    public ResponseEntity<RouteResponse> optimizeRoute() {
        Courier courier = getCurrentCourier();
        return ResponseEntity.ok(orderService.optimizeRoute(courier.getId()));
    }

    /**
     * PUT /api/v1/courier/orders/{id}/status — update order status.
     * Couriers can transition: ASSIGNED → IN_TRANSIT, IN_TRANSIT → DELIVERED or FAILED.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request
    ) {
        OrderResponse response = orderService.updateCourierStatus(id, request.status());
        return ResponseEntity.ok(response);
    }

    /* ---- Private helpers ---- */

    private Courier getCurrentCourier() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return courierRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found for current user"));
    }
}
