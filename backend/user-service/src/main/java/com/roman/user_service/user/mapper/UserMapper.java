package com.roman.user_service.user.mapper;

import com.roman.user_service.user.dto.UserResponse;
import com.roman.user_service.user.model.User;

public class UserMapper {

    public static UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getPrename(),
                user.getLastname(),
                user.getUsername(),
                user.getStreet(),
                user.getHouseNumber(),
                user.getPostalCode(),
                user.getTown(),
                user.getCountry(),
                user.getEmail(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
