package com.roman.user_service.apiRequest;

import com.roman.user_service.apiRequest.dto.JwtPayload;
import com.roman.user_service.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/securityService")
public class ApiSecurityController {
    private final JwtService jwtService;

    public ApiSecurityController(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    //Umstellung auf Microservice
    @GetMapping("/validate")
    public ResponseEntity<JwtPayload> validate(@RequestParam String token) {

        Claims claims = jwtService.extractAllClaims(token);

        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);

        JwtPayload payload = new JwtPayload(userId, role);

        return ResponseEntity.ok(payload);
    }

}
