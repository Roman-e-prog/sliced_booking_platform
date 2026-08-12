package com.roman.user_service.security.auth.service;

import com.roman.user_service.security.model.RefreshToken;
import com.roman.user_service.security.repository.RefreshTokenRepository;
import com.roman.user_service.user.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);
        token.setReplacedByToken(null);

        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setRevoked(true);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        newToken.setRevoked(false);

        oldToken.setReplacedByToken(newToken.getToken());

        refreshTokenRepository.save(oldToken);
        return refreshTokenRepository.save(newToken);
    }

    public void revokeUserTokens(User user) {
        refreshTokenRepository.revokeAllTokensForUser(user.getId());
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

}

