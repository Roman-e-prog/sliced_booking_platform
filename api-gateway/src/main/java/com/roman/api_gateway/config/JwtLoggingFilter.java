package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class JwtLoggingFilter {

    @Bean
    public GlobalFilter jwtLoggingFilter() {
        return (exchange, chain) -> {

            ServerWebExchange ex = exchange;

            // -------------------------------
            // 1. Request Logging
            // -------------------------------
            log.info("Incoming request: {} {}",
                    ex.getRequest().getMethod(),
                    ex.getRequest().getURI());

            // -------------------------------
            // 2. JWT aus dem Header lesen
            // -------------------------------
            String authHeader = ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null) {
                log.info("JWT detected: {}", authHeader);
            } else {
                log.info("No JWT provided");
            }

            // -------------------------------
            // 3. Header wird automatisch weitergegeben
            //    → Gateway verändert nichts
            //    → Microservices prüfen JWT selbst
            // -------------------------------

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {

                        // -------------------------------
                        // 4. Response Logging
                        // -------------------------------
                        log.info("Outgoing response: {} for {}",
                                ex.getResponse().getStatusCode(),
                                ex.getRequest().getURI());
                    }));
        };
    }
}
