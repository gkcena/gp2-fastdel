package com.example.backend.service.impl;

import com.example.backend.dto.RouteResponse;
import com.example.backend.service.RouteCacheService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
public class NoOpRouteCacheServiceImpl implements RouteCacheService {

    @Override
    public Optional<RouteResponse> getCachedRoute(UUID courierId) {
        return Optional.empty();
    }

    @Override
    public void cacheRoute(UUID courierId, RouteResponse response) {
        // Do nothing
    }

    @Override
    public void invalidateCache(UUID courierId) {
        // Do nothing
    }
}
