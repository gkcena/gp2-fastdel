package com.example.backend.service;

import com.example.backend.dto.CreateOrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.RouteResponse;
import com.example.backend.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request, UUID staffUserId);
    OrderResponse assignCourier(UUID orderId, UUID courierId);
    OrderResponse updateStatus(UUID orderId, OrderStatus newStatus);
    OrderResponse updateCourierStatus(UUID orderId, OrderStatus newStatus);
    List<OrderResponse> listOrders();
    List<OrderResponse> listPendingOrders();
    List<OrderResponse> listCourierOrders(UUID courierId);
    OrderResponse getByBarcode(String barcode);
    RouteResponse optimizeRoute(UUID courierId);
}
