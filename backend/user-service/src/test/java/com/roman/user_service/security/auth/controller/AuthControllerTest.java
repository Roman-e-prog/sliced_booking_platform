package com.roman.user_service.security.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.user_service.security.auth.dto.*;
import com.roman.user_service.security.auth.service.AuthenticationService;
import com.roman.user_service.security.auth.service.LogoutService;
import com.roman.user_service.security.auth.service.PasswordResetTokenService;
import com.roman.user_service.security.jwt.JwtService;
import com.roman.user_service.security.model.PasswordResetToken;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)//for not getting default spring security filters
public class AuthControllerTest {

    //if a Spring context creates beans on its own that can be utilized without mocking,
    // we can use the @Autowired annotation to inject them
    @Autowired
    private MockMvc mockMvc;
    //I mock all the services
    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private PasswordResetTokenService passwordResetTokenService;
    @MockitoBean
    private LogoutService logoutService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper; // for JSON serialization
    @Test
    void should_register_a_user() throws Exception{
        //Arrange: request DTO
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
        User user = new User();

        when(authenticationService.register(any(RegisterRequest.class)))
                .thenReturn(user);
        // Act + Assert: perform HTTP POST
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }
    @Test
        void should_login_the_user_with_status_200() throws Exception{
        // Arrange: request DTO
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = "roman@example.com";
        loginRequest.password = "password";
        //I mock the authResponse
        AuthResponse authResponse = new AuthResponse("access-token-123", null);
        //now I assert when the login function is called,
        // with any instance of the loginRequest class and any string the authResponse is triggered
        when(authenticationService.login(any(LoginRequest.class), any()))
                .thenReturn(authResponse);
        // Act + Assert: perform HTTP POST
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("access-token-123"));
    }
    @Test
    void should_request_password_reset_and_return_token() throws Exception {
        // Arrange
        PasswordResetRequest request = new PasswordResetRequest();
        request.email = "roman@example.com";

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("access-token-123");

        when(passwordResetTokenService.passwordReset(any(PasswordResetRequest.class)))
                .thenReturn(token);

        // Act + Assert
        mockMvc.perform(
                        post("/auth/passwordReset")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("access-token-123"));

        verify(passwordResetTokenService, times(1))
                .passwordReset(any(PasswordResetRequest.class));
    }
    @Test
    void should_change_password_and_return_200() throws Exception {
        PasswordResetDTO dto = new PasswordResetDTO();
        dto.token = "access-token-123";
        dto.password = "newPassword";

        doNothing().when(passwordResetTokenService).changePassword(any(PasswordResetDTO.class));

        mockMvc.perform(
                        post("/auth/changePassword")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        verify(passwordResetTokenService, times(1))
                .changePassword(any(PasswordResetDTO.class));
    }


}
