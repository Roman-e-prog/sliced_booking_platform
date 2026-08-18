package com.roman.user_service.security.jwt;

import com.roman.user_service.security.user.CustomUserDetails;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //I need the jwt service and the user repository
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. No header or not Bearer → skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract token
        String token = authHeader.substring(7); // after "Bearer "

        // 3. Extract username (email) from token
        Long userId = jwtService.extractUserId(token);

        // 4. Only set auth if not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = userRepository.findById(userId).orElse(null);
            CustomUserDetails userDetails = new CustomUserDetails(user);


            if (user != null && jwtService.isTokenValid(token, userDetails)) {
                // 5. Build Authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // principal
                                null, // no credentials here
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. Put it into SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
