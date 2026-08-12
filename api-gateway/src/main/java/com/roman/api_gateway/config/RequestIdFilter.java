package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Configuration
public class RequestIdFilter {

    @Bean
    public GlobalFilter requestIdFilter() {
        return (exchange, chain) -> {

            // 1. Neue Request-ID erzeugen
            String requestId = UUID.randomUUID().toString();

            // 2. Request-ID in den Header schreiben
            exchange.getRequest()
                    .mutate()
                    .header("X-Request-ID", requestId)
                    .build();

            log.info("Gateway Request-ID {} for {} {}",
                    requestId,
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("Gateway Response {} for Request-ID {}",
                                exchange.getResponse().getStatusCode(),
                                requestId);
                    }));
        };
    }

}
