package com.roman.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class LoggingFilter {

    @Bean
    public GlobalFilter gatewayLoggingFilter() {
        return (exchange, chain) -> {

            ServerWebExchange ex = exchange;

            log.info("Incoming request: {} {}",
                    ex.getRequest().getMethod(),
                    ex.getRequest().getURI());

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("Outgoing response: {} for {}",
                                ex.getResponse().getStatusCode(),
                                ex.getRequest().getURI());
                    }));
        };
    }
}
