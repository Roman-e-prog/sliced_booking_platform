package com.roman.price_service.webClient.dto;

public record JwtPayload(
        Long userId,
        String role
) { }
