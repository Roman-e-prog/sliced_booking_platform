package com.roman.room_service.webClient.dto;


public record JwtPayload(
        Long userId,
        String role
) { }
