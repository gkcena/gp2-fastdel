package com.example.backend.service;

import com.example.backend.dto.RouteResponse;
import java.util.Optional;
import java.util.UUID;

public interface RouteCacheService {
    Optional<RouteResponse> getCachedRoute(UUID courierId);
    void cacheRoute(UUID courierId, RouteResponse response);
    void invalidateCache(UUID courierId);
}
