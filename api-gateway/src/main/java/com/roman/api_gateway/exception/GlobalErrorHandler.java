package com.roman.api_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatusCode;
@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        // Stacktrace loggen
        log.error("Gateway ERROR for request {}", exchange.getRequest().getURI(), ex);

        // Default-Status
        HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        // ResponseStatusException korrekt behandeln
        if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            statusCode = rse.getStatusCode();
        }

        exchange.getResponse().setStatusCode(statusCode);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = """
            {
                "status": %d,
                "error": "%s",
                "exception": "%s",
                "message": "%s",
                "path": "%s"
            }
            """.formatted(
                statusCode.value(),
                statusCode.toString(),
                ex.getClass().getName(),
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return exchange.getResponse()
                .writeWith(Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(json.getBytes())
                ));
    }
}
