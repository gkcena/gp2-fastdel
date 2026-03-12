package com.example.backend.controller;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Staff order management controller — /api/v1/staff/orders
 * All endpoints require STAFF role (enforced by SecurityConfig).
 *
 * Rules enforced:
 *  • No business logic — delegates to OrderService
 *  • DTO-only request/response
 */
@RestController
@RequestMapping("/api/v1/staff/orders")
public class StaffOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public StaffOrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    /**
     * POST /api/v1/staff/orders — create a new order.
     * The staff user's ID is extracted from the JWT.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User staff = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Staff user not found"));

        OrderResponse response = orderService.createOrder(request, staff.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/staff/orders — list all orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(orderService.listOrders());
    }

    /**
     * GET /api/v1/staff/orders/barcode/{barcode} — get order by barcode.
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<OrderResponse> getByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(orderService.getByBarcode(barcode));
    }
}
