package com.roman.user_service.user.config;

import com.roman.user_service.booking.repository.BookingRepository;
import com.roman.user_service.security.user.CustomUserDetails;
import com.roman.user_service.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
    private final UserRepository userRepository;

    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = principal.getId();

        return userRepository.findById(userId)
                .map(u -> u.getId().equals(currentUserId))
                .orElse(false);
    }
}
