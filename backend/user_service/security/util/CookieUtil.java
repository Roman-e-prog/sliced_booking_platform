package com.roman.user_service.security.util;

import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String cookie = String.format(
                "%s=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                name, value, maxAge
        );
        response.addHeader("Set-Cookie", cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        String cookie = String.format(
                "%s=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Strict",
                name
        );
        response.addHeader("Set-Cookie", cookie);
    }
}
