package com.roman.user_service.security.auth.service;

import com.roman.user_service.security.util.CookieUtil;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LogoutService {

    private final RefreshTokenService refreshTokenService;


    public LogoutService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    public void logout(String refreshToken, HttpServletResponse response) {
        refreshTokenService.deleteByToken(refreshToken);
        CookieUtil.deleteCookie(response, "refreshToken");
    }

}

