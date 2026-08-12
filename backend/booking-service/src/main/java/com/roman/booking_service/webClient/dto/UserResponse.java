package com.roman.booking_service.webClient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String prename,
        String lastname,
        String username,
        String street,
        String houseNumber,
        Integer postalCode,
        String town,
        String country,
        String email,
        LocalDate birthDate,
        String role,
        LocalDateTime createdAt
) { }

