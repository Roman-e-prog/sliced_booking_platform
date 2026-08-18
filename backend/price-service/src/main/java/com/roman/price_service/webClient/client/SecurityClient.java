package com.roman.price_service.webClient.client;

import com.roman.price_service.webClient.dto.JwtPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SecurityClient {

    private final WebClient webClient;

    public SecurityClient(WebClient userWebClient) {
        this.webClient = userWebClient;
    }

    public JwtPayload validateToken(String token) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("securityService/validate")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(JwtPayload.class)
                .block();
    }
}

