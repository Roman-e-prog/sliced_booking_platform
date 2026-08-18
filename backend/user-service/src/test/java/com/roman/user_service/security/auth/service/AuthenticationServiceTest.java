package com.roman.user_service.security.auth.service;

import com.roman.user_service.exceptions.InvalidException;
import com.roman.user_service.security.auth.dto.AuthResponse;
import com.roman.user_service.security.auth.dto.LoginRequest;
import com.roman.user_service.security.auth.dto.RegisterRequest;
import com.roman.user_service.security.jwt.JwtService;
import com.roman.user_service.security.model.RefreshToken;
import com.roman.user_service.security.user.CustomUserDetails;
import com.roman.user_service.security.util.CookieUtil;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);

    private final AuthenticationService authenticationService =
            new AuthenticationService(userRepository, passwordEncoder, jwtService, refreshTokenService);

    @Test
    void register_createsNewUser() {
        RegisterRequest request = new RegisterRequest();
        request.prename="Roman";
        request.lastname="Test";
        request.username="RomanTest";
        request.email="roman@example.com";
        request.street="TestStreet";
        request.houseNumber="45A";
        request.postalCode=45458;
        request.town="TestTown";
        request.country="Germany";
        request.birthDate="1990-01-01";
        request.password="password";

        when(userRepository.findByEmail("roman@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password")).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setEmail("roman@example.com");
        savedUser.setPassword("encoded");
        savedUser.setBirthDate(LocalDate.parse("1990-01-01"));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authenticationService.register(request);

        assertEquals("roman@example.com", result.getEmail());
        assertEquals("encoded", result.getPassword());
        assertEquals(LocalDate.parse("1990-01-01"), result.getBirthDate());

        verify(userRepository).findByEmail("roman@example.com");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }
    @Test
    void register_throwsException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.email = "roman@example.com";

        User existingUser = new User();
        existingUser.setEmail("roman@example.com");

        when(userRepository.findByEmail("roman@example.com"))
                .thenReturn(Optional.of(existingUser));

        InvalidException exception = assertThrows(
                InvalidException.class,
                () -> authenticationService.register(request)
        );

        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository).findByEmail("roman@example.com");
        verify(userRepository, never()).save(any());
    }
    @Test
    void login_logsInUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("roman@example.com");
        existingUser.setPassword("encodedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = "roman@example.com";
        loginRequest.password = "password";
        //when the userRepository finds the email, then return my mockedUser
        when(userRepository.findByEmail("roman@example.com"))
                .thenReturn(Optional.of(existingUser));
        //when the mocked passwordEncoder matches the test values return true
        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);
        //when this jwtService does what it should,set up a mocked token
        when(jwtService.generateToken(any(CustomUserDetails.class)))
                .thenReturn("access-token-123");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-456");
        //when this refreshTokenService does what it should, then return this refreshToken
        when(refreshTokenService.createRefreshToken(existingUser))
                .thenReturn(refreshToken);
        //mock the HttpResponse
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act
        AuthResponse authResponse = authenticationService.login(loginRequest, response);

        // Assert
        assertEquals("access-token-123", authResponse.token());

        verify(userRepository).findByEmail("roman@example.com");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtService).generateToken(any(CustomUserDetails.class));
        verify(refreshTokenService).createRefreshToken(existingUser);

        // CookieUtil.addCookie(...) is static, so we cannot verify it easily here.
        // We will test cookie behavior in the controller test later.
    }
    @Test
    void login_should_fail_when_user_not_found() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = "roman@test.com";
        loginRequest.password = "password";

        when(userRepository.findByEmail("roman@test.com"))
                .thenReturn(Optional.empty());

        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(loginRequest, response)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail("roman@test.com");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }
    @Test
    void login_should_fail_with_invalid_password() {
        // Arrange
        User existingUser = new User();
        existingUser.setEmail("roman@example.com");
        existingUser.setPassword("encodedPassword");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = "roman@example.com";
        loginRequest.password = "wrongPassword";

        when(userRepository.findByEmail("roman@example.com"))
                .thenReturn(Optional.of(existingUser));

        // Password does NOT match
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(loginRequest, response)
        );

        assertEquals("Invalid password", exception.getMessage());

        // Verify behavior
        verify(userRepository).findByEmail("roman@example.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(jwtService, never()).generateToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }
@Test
    void shouldRefreshTheToken(){
        //I need a user
    User existingUser = new User();
    existingUser.setEmail("roman@example.com");
    existingUser.setPassword("encodedPassword");
    // I need a token on this user, because I want to test if it finds this user by token
    RefreshToken oldRefreshToken = new RefreshToken();
    oldRefreshToken.setToken("old-refresh-token-123");
    oldRefreshToken.setUser(existingUser);
    //I need a string to simulate the cookie string
    String oldRefreshTokenString = "old-refresh-token-123";
    when(refreshTokenService.validateRefreshToken(oldRefreshTokenString))
            .thenReturn(oldRefreshToken);
        //I need a new token
    when(jwtService.generateToken(any(CustomUserDetails.class)))
            .thenReturn("new-token-456");

    RefreshToken newToken = new RefreshToken();
    newToken.setToken("new-token-789");
    when(refreshTokenService.rotateRefreshToken(oldRefreshToken))
            .thenReturn(newToken);
    HttpServletResponse response = mock(HttpServletResponse.class);
    AuthResponse authResponse = authenticationService.refresh(oldRefreshTokenString, response);
    assertEquals("new-token-456", authResponse.token());

    verify(refreshTokenService).validateRefreshToken(oldRefreshTokenString);
    verify(jwtService).generateToken(any(CustomUserDetails.class));
    verify(refreshTokenService).rotateRefreshToken(oldRefreshToken);


    }

}
