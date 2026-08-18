package com.roman.user_service.security.auth.service;

import com.roman.user_service.security.auth.dto.PasswordResetDTO;
import com.roman.user_service.security.auth.dto.PasswordResetRequest;
import com.roman.user_service.security.model.PasswordResetToken;
import com.roman.user_service.security.model.RefreshToken;
import com.roman.user_service.security.repository.PasswordResetTokenRepository;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository,
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordResetToken passwordReset(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public void changePassword(PasswordResetDTO request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.password));
        userRepository.save(user);
        passwordResetTokenRepository.delete(token);
    }
}

