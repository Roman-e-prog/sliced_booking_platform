package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Configuration
public class GatewayLoggingFilter {

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {

            // -----------------------------------------
            // 1. Request-ID erzeugen
            // -----------------------------------------
            String requestId = UUID.randomUUID().toString();

            // Header hinzufügen (mutate = WebFlux-Builder)
            ServerWebExchange ex = exchange.mutate()
                    .request(r -> r.headers(h -> h.add("X-Request-ID", requestId)))
                    .build();

            // -----------------------------------------
            // 2. Request Logging
            // -----------------------------------------
            log.info("[{}] Incoming request: {} {}",
                    requestId,
                    ex.getRequest().getMethod(),
                    ex.getRequest().getURI());

            // -----------------------------------------
            // 3. JWT Logging
            // -----------------------------------------
            String authHeader = ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                log.info("[{}] JWT detected: {}", requestId, authHeader);
            } else {
                log.info("[{}] No JWT provided", requestId);
            }

            // -----------------------------------------
            // 4. Weiterleiten an Zielservice
            // -----------------------------------------
            return chain.filter(ex)
                    .then(Mono.fromRunnable(() -> {

                        // -----------------------------------------
                        // 5. Response Logging
                        // -----------------------------------------
                        log.info("[{}] Outgoing response: {} for {}",
                                requestId,
                                ex.getResponse().getStatusCode(),
                                ex.getRequest().getURI());
                    }));
        };
    }
}
