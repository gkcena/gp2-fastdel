package com.example.backend.dto;

import java.util.List;

public record RouteResponse(
        List<RouteStop> stops,
        String courierStartAddress
) {
}
