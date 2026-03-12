package com.example.backend.controller;

import com.example.backend.dto.AssignCourierRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.UpdateStatusRequest;
import com.example.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin order management controller — /api/v1/admin/orders
 * All endpoints require ADMIN role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to OrderService
 *  • DTO-only request/response
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /api/v1/admin/orders — list all orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    /**
     * GET /api/v1/admin/orders/pending — list PENDING orders.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> listPendingOrders() {
        return ResponseEntity.ok(orderService.listPendingOrders());
    }

    /**
     * POST /api/v1/admin/orders/{id}/assign — assign courier to order.
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<OrderResponse> assignCourier(
            @PathVariable UUID id,
            @RequestBody AssignCourierRequest request
    ) {
        OrderResponse response = orderService.assignCourier(id, request.courierId());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/admin/orders/{id}/status — update order status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusRequest request
    ) {
        OrderResponse response = orderService.updateStatus(id, request.status());
        return ResponseEntity.ok(response);
    }
}
