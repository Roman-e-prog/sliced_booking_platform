package com.roman.user_service.apiRequest.dto;

public record JwtPayload(
        Long userId,   // email oder userId – je nachdem, was du im Token als subject setzt
        String role
) {}

