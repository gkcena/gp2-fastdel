package com.example.backend.service.impl;

import com.example.backend.dto.RouteResponse;
import com.example.backend.service.RouteCacheService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRouteCacheServiceImpl implements RouteCacheService {

    private final ConcurrentHashMap<UUID, CachedRoute> cache = new ConcurrentHashMap<>();

    private record CachedRoute(RouteResponse response, Instant cachedAt) {}

    @Override
    public Optional<RouteResponse> getCachedRoute(UUID courierId) {
        CachedRoute cachedRoute = cache.get(courierId);
        if (cachedRoute != null) {
            if (Instant.now().isBefore(cachedRoute.cachedAt().plus(20, ChronoUnit.MINUTES))) {
                return Optional.of(cachedRoute.response());
            } else {
                cache.remove(courierId);
            }
        }
        return Optional.empty();
    }

    @Override
    public void cacheRoute(UUID courierId, RouteResponse response) {
        cache.put(courierId, new CachedRoute(response, Instant.now()));
    }

    @Override
    public void invalidateCache(UUID courierId) {
        cache.remove(courierId);
    }
}
