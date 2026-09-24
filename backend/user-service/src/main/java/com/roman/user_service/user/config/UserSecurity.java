package com.roman.user_service.user.config;

import com.roman.user_service.security.user.CustomUserDetails;
import com.roman.user_service.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
@Slf4j
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

        log.info("Checking if user {} is the owner of user {}", currentUserId, userId);
        return userRepository.findById(userId)
                .map(u -> u.getUserId().equals(currentUserId))
                .orElse(false);
    }
}
