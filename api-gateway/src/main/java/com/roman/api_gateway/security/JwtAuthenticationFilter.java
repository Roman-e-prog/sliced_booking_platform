package com.roman.api_gateway.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;
import java.util.List;
@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    var request = exchange.getRequest();
    var path = request.getURI().getPath();

    // 1. OPTIONS IMMER durchlassen (Preflight)
    if (request.getMethod().name().equals("OPTIONS")) {
        return chain.filter(exchange);
    }

    // 2. Auth-Routen IMMER durchlassen
    if (path.startsWith("/api/auth/") || path.equals("/api/user/register")) {
        return chain.filter(exchange);
    }

    // 3. Token prüfen
    var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return chain.filter(exchange);
    }

    var token = authHeader.substring(7);

    Claims claims;
    try {
        claims = jwtUtil.parseToken(token);
    } catch (Exception e) {
        // WICHTIG: NICHT abbrechen!
        return chain.filter(exchange);
    }

    var roles = jwtUtil.getRoles(claims);
    var user = jwtUtil.getUsername(claims);

    var mutatedRequest = request.mutate()
            .header("X-User-Name", user)
            .header("X-User-Roles", roles == null ? "" : String.join(",", roles))
            .build();

    var mutatedExchange = exchange.mutate().request(mutatedRequest).build();
    return chain.filter(mutatedExchange);
}

}
