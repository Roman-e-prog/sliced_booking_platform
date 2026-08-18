package com.roman.user_service.user.dto;

import com.roman.user_service.user.model.User;

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
         User.Role role,
         LocalDateTime createdAt
) { }
