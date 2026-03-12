package com.example.backend.service;

import com.example.backend.dto.RouteStop;
import com.example.backend.model.Order;

import java.util.List;

public interface GoogleMapsService {
    List<RouteStop> optimizeRoute(List<Order> orders);
}
