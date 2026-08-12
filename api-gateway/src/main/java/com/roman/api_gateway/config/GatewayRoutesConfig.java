package com.roman.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("user-service", r -> r
                        .path("/api/user/**")
                        .uri("http://localhost:8084"))

                .route("booking-service", r -> r
                        .path("/api/booking/**")
                        .uri("http://localhost:8081"))

                .route("room-service", r -> r
                        .path("/api/room/**")
                        .uri("http://localhost:8083"))

                .route("price-service", r -> r
                        .path("/api/price/**")
                        .uri("http://localhost:8082"))

                .build();
    }
}


