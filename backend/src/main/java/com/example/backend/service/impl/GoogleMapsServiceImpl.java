package com.example.backend.service.impl;

import com.example.backend.dto.RouteStop;
import com.example.backend.model.Order;
import com.example.backend.service.GoogleMapsService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleMapsServiceImpl implements GoogleMapsService {

    private final WebClient webClient;
    private final String apiKey;

    public GoogleMapsServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${google.maps.api-key}") String apiKey
    ) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com").build();
        this.apiKey = apiKey;
    }

    @Override
    public List<RouteStop> optimizeRoute(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        if (orders.size() == 1) {
            Order order = orders.get(0);
            return List.of(new RouteStop(
                    order.getId(),
                    order.getBarcode(),
                    order.getCustomerName(),
                    order.getDeliveryAddress(),
                    1
            ));
        }

        Order lastOrder = orders.get(orders.size() - 1);
        String destination = lastOrder.getDeliveryAddress();

        StringBuilder waypoints = new StringBuilder("optimize:true");
        for (int i = 0; i < orders.size() - 1; i++) {
            waypoints.append("|").append(orders.get(i).getDeliveryAddress());
        }

        String responseString = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/directions/json")
                        .queryParam("origin", "current location")
                        .queryParam("destination", destination)
                        .queryParam("waypoints", waypoints.toString())
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode response = null;
        if (responseString != null) {
            try {
                ObjectMapper mapper = JsonMapper.builder().build();
                response = mapper.readValue(responseString, JsonNode.class);
            } catch (JacksonException e) {
                // ignore parsing error
            }
        }

        List<RouteStop> stops = new ArrayList<>();

        if (response != null && "OK".equals(response.path("status").asString())) {
            JsonNode routes = response.path("routes");
            if (!routes.isEmpty()) {
                JsonNode waypointOrder = routes.get(0).path("waypoint_order");

                int optimizedOrder = 1;
                for (JsonNode indexNode : waypointOrder) {
                    int originalIndex = indexNode.asInt();
                    Order order = orders.get(originalIndex);
                    stops.add(new RouteStop(
                            order.getId(),
                            order.getBarcode(),
                            order.getCustomerName(),
                            order.getDeliveryAddress(),
                            optimizedOrder++
                    ));
                }

                stops.add(new RouteStop(
                        lastOrder.getId(),
                        lastOrder.getBarcode(),
                        lastOrder.getCustomerName(),
                        lastOrder.getDeliveryAddress(),
                        optimizedOrder
                ));
            }
        } else {
            int orderIndex = 1;
            for (Order order : orders) {
                stops.add(new RouteStop(
                        order.getId(),
                        order.getBarcode(),
                        order.getCustomerName(),
                        order.getDeliveryAddress(),
                        orderIndex++
                ));
            }
        }

        return stops;
    }
}
