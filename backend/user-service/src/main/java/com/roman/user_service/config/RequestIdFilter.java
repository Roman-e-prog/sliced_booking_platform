package com.roman.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 1. Request-ID aus dem Header lesen
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-ID");

        if (requestId != null) {
            // 2. Request-ID in MDC setzen (für Logging)
            MDC.put("requestId", requestId);
            log.info("Received Request-ID {}", requestId);
        }

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    // 3. MDC wieder leeren
                    MDC.clear();
                });
    }
}

