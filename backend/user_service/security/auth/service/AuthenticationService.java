package com.roman.user_service.security.auth.service;

import com.roman.user_service.exceptions.InvalidCredentialsException;
import com.roman.user_service.exceptions.InvalidException;
import com.roman.user_service.security.auth.dto.AuthResponse;
import com.roman.user_service.security.auth.dto.LoginRequest;
import com.roman.user_service.security.auth.dto.RegisterRequest;
import com.roman.user_service.security.jwt.JwtService;
import com.roman.user_service.security.model.RefreshToken;
import com.roman.user_service.security.user.CustomUserDetails;
import com.roman.user_service.security.util.CookieUtil;
import com.roman.user_service.user.dto.UserResponse;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new InvalidException("Email already in use");
        }

        User user = new User();
        user.setPrename(request.prename);
        user.setLastname(request.lastname);
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setStreet(request.street);
        user.setHouseNumber(request.houseNumber);
        user.setPostalCode(request.postalCode);
        user.setTown(request.town);
        user.setCountry(request.country);
        LocalDate parsedDate = Instant.parse(request.birthDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        user.setBirthDate(parsedDate);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(User.Role.USER);

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        // 1. Find user
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // 2. Check password
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 3. Wrap in CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 4. Generate access token (for Authorization header)
        String accessToken = jwtService.generateToken(userDetails);

        // 5. Generate refresh token (entity)
        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user);

        // 6. Extract token string
        String refreshToken = refreshTokenEntity.getToken();

        // 7. Set HttpOnly cookie with refresh token
        CookieUtil.addCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60);

        // 8. Return access token in body
        return new AuthResponse(accessToken,new UserResponse(
                user.getId(),
                user.getPrename(),
                user.getLastname(),
                user.getUsername(),
                user.getStreet(),
                user.getHouseNumber(),
                user.getPostalCode(),
                user.getTown(),
                user.getCountry(),
                user.getEmail(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt()

        ));
    }
    public AuthResponse refresh(String oldRefreshToken, HttpServletResponse response) {

        // 1. Validate the old refresh token
        RefreshToken token = refreshTokenService.validateRefreshToken(oldRefreshToken);

        // 2. Get the user
        User user = token.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 3. Generate a new access token
        String newAccessToken = jwtService.generateToken(userDetails);

        // 4. Rotate the refresh token
        RefreshToken newToken = refreshTokenService.rotateRefreshToken(token);
        String newRefreshToken = newToken.getToken();

        // 5. Update the cookie
        CookieUtil.addCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60);

        // 6. Return the new access token
        return new AuthResponse(newAccessToken, new UserResponse(
                user.getId(),
                user.getPrename(),
                user.getLastname(),
                user.getUsername(),
                user.getStreet(),
                user.getHouseNumber(),
                user.getPostalCode(),
                user.getTown(),
                user.getCountry(),
                user.getEmail(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt()
        ));
    }
    public Boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
    public Boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}

