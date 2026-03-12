package com.example.backend.service.impl;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.RouteResponse;
import com.example.backend.model.*;
import com.example.backend.repository.CourierRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.GoogleMapsService;
import com.example.backend.service.OrderService;
import com.example.backend.service.RouteCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String BARCODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BARCODE_RANDOM_LENGTH = 4;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourierRepository courierRepository;
    private final GoogleMapsService googleMapsService;
    private final RouteCacheService routeCacheService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CourierRepository courierRepository,
            GoogleMapsService googleMapsService,
            RouteCacheService routeCacheService
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.courierRepository = courierRepository;
        this.googleMapsService = googleMapsService;
        this.routeCacheService = routeCacheService;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, UUID staffUserId) {
        User staff = userRepository.findById(staffUserId)
                .orElseThrow(() -> new IllegalArgumentException("Staff user not found"));

        Order order = new Order();
        order.setBarcode(generateBarcode());
        order.setCustomerName(request.customerName());
        order.setCustomerPhone(request.customerPhone());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy(staff);

        order = orderRepository.save(order);
        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderResponse assignCourier(UUID orderId, UUID courierId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Order can only be assigned when status is PENDING, current: " + order.getStatus());
        }

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new IllegalArgumentException("Courier not found"));

        order.setAssignedCourier(courier);
        order.setStatus(OrderStatus.ASSIGNED);
        order = orderRepository.save(order);

        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return OrderResponse.from(order);
    }

    @Override
    @Transactional
    public OrderResponse updateCourierStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        boolean valid = switch (order.getStatus()) {
            case ASSIGNED -> newStatus == OrderStatus.IN_TRANSIT;
            case IN_TRANSIT -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.FAILED;
            default -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException("Invalid status transition");
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return OrderResponse.from(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listPendingOrders() {
        return orderRepository.findAllByStatus(OrderStatus.PENDING)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listCourierOrders(UUID courierId) {
        return orderRepository.findAllByAssignedCourierId(courierId)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByBarcode(String barcode) {
        Order order = orderRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with barcode: " + barcode));
        return OrderResponse.from(order);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse optimizeRoute(UUID courierId) {
        Optional<RouteResponse> cachedRoute = routeCacheService.getCachedRoute(courierId);
        if (cachedRoute.isPresent()) {
            return cachedRoute.get();
        }

        List<Order> assignedOrders = orderRepository.findAllByAssignedCourierId(courierId)
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.ASSIGNED)
                .toList();

        if (assignedOrders.isEmpty()) {
            throw new IllegalArgumentException("No assigned orders found");
        }

        RouteResponse response = new RouteResponse(
                googleMapsService.optimizeRoute(assignedOrders),
                "Current Location"
        );
        routeCacheService.cacheRoute(courierId, response);
        return response;
    }

    private String generateBarcode() {
        String datePart = LocalDate.now().format(DATE_FMT);
        StringBuilder randomPart = new StringBuilder(BARCODE_RANDOM_LENGTH);
        for (int i = 0; i < BARCODE_RANDOM_LENGTH; i++) {
            randomPart.append(BARCODE_CHARS.charAt(RANDOM.nextInt(BARCODE_CHARS.length())));
        }
        return "FD-" + datePart + "-" + randomPart;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case ASSIGNED -> next == OrderStatus.IN_TRANSIT;
            case IN_TRANSIT -> next == OrderStatus.DELIVERED || next == OrderStatus.FAILED;
            default -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + current + " → " + next);
        }
    }
}
