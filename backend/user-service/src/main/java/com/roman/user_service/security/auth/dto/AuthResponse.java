package com.roman.user_service.security.auth.dto;

import com.roman.user_service.user.dto.UserResponse;

public record AuthResponse(String token, UserResponse user) {
}
