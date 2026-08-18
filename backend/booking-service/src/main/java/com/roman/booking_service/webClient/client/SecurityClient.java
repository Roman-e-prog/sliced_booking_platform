package com.roman.booking_service.webClient.client;

import com.roman.booking_service.webClient.dto.JwtPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;
@Component
public class SecurityClient {

    private final WebClient webClient;

    public SecurityClient(@Qualifier("userWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public JwtPayload validateToken(String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/securityService/validate")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(JwtPayload.class)
                .block();
    }
}

