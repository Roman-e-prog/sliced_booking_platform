package com.roman.user_service.security.auth.controller;
//import all dtos and other classes
import com.roman.user_service.security.auth.dto.*;
import com.roman.user_service.security.auth.service.LogoutService;
import com.roman.user_service.security.auth.service.PasswordResetTokenService;
import com.roman.user_service.security.model.PasswordResetToken;
import com.roman.user_service.user.model.User;
import com.roman.user_service.security.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final LogoutService logoutService;

    public AuthController(AuthenticationService authenticationService,
                          PasswordResetTokenService passwordResetTokenService,
                          LogoutService logoutService) {

        this.authenticationService = authenticationService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.logoutService = logoutService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println(request);
            User user = authenticationService.register(request);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            // Log the full stacktrace for debugging
            e.printStackTrace();

            // Return a readable error to the client
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/uniqueUsername")
    public ResponseEntity<Boolean> validateUsername(@RequestParam String username){
        return ResponseEntity.ok(authenticationService.existsByUsername(username));
    }
    @GetMapping("/uniqueEmail")
    public ResponseEntity<Boolean> validateEmail(@RequestParam String email){
        return ResponseEntity.ok(authenticationService.existsByEmail(email));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authenticationService.login(request, response);
        return ResponseEntity.ok(authResponse); }

    @PostMapping("/passwordReset")
    public ResponseEntity<PasswordResetResponse> passwordReset(
            @Valid @RequestBody PasswordResetRequest request) {

        PasswordResetToken token = passwordResetTokenService.passwordReset(request);
        return ResponseEntity.ok(new PasswordResetResponse(token.getToken()));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordResetDTO request) {

        passwordResetTokenService.changePassword(request);
        return ResponseEntity.ok().build();//build for empty body
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        logoutService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authenticationService.refresh(refreshToken, response);
        return ResponseEntity.ok(authResponse);
    }


}
