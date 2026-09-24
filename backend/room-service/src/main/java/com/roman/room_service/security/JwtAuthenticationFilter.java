package com.roman.room_service.security;

import com.roman.room_service.webClient.client.SecurityClient;
import com.roman.room_service.webClient.dto.JwtPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityClient securityClient;

    public JwtAuthenticationFilter(SecurityClient securityClient) {
        this.securityClient = securityClient;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization Header prüfen
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Token extrahieren
        String token = authHeader.substring(7);

        // 3. Token beim User-Service validieren
        JwtPayload payload = securityClient.validateToken(token);

        if (payload != null) {

            // 4. Rolle in ROLE_ Format umwandeln
            String role = payload.role(); // z.B. "ADMIN"
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            List<GrantedAuthority> authorities = List.of(authority);

            // 5. Authentication erzeugen
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            payload.userId(), // principal
                            null,
                            authorities
                    );

            // 6. Logging der Authorities
            log.info("Authentication set in SecurityContextHolder: principal={}, authorities={}",
                    authentication.getPrincipal(),
                    authentication.getAuthorities()
            );

            // 7. SecurityContext setzen
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. Weiter in der FilterChain
        filterChain.doFilter(request, response);
    }
}
