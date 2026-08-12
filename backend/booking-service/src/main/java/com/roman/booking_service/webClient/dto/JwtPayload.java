package com.roman.booking_service.webClient.dto;

public record JwtPayload(
        Long userId,
        String role
) { }
