package com.roman.user_service.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.user_service.security.jwt.JwtService;
import com.roman.user_service.user.dto.UserRequest;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import com.roman.user_service.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Log4j2
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper; // for JSON serialization
    @Test
    void should_update_user() throws Exception{
        User existing = new User();
        existing.setId(1L);
        existing.setPrename("Roman");
        existing.setLastname("TestName");
        existing.setUsername("TestRoman");
        existing.setStreet("Teststreet");
        existing.setHouseNumber("45A");
        existing.setPostalCode(12345);
        existing.setTown("Testtown");
        existing.setCountry("TestCountry");
        // Mock service
        when(userService.updateUser(eq(1L), any(UserRequest.class)))
                .thenReturn(existing);
        UserRequest userRequest = new UserRequest();
        userRequest.prename = "Roman";
        userRequest.lastname = "Testname";
        userRequest.username = "TestRoman";
        userRequest.street = "Teststreet";
        userRequest.houseNumber = "45A";
        userRequest.postalCode = 12345;
        userRequest.town = "Testtown";
        userRequest.country = "TestCountry";

        // Act + Assert
        mockMvc.perform(
                        put("/user/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest))
                )
                .andDo(result -> log.error("e: ", result.getResolvedException()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void should_find_one_user() throws Exception{
        User existing = new User();
        existing.setId(1L);
        existing.setPrename("Roman");
        existing.setLastname("TestName");
        existing.setUsername("TestRoman");
        existing.setStreet("Teststreet");
        existing.setHouseNumber("45A");
        existing.setPostalCode(12345);
        existing.setTown("Testtown");
        existing.setCountry("TestCountry");
        // Mock service
        when(userService.findOrThrow(1L))
                .thenReturn(existing);

        // Act + Assert
        mockMvc.perform(get("/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Optional: assert JSON fields
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.prename").value("Roman"))
                .andExpect(jsonPath("$.lastname").value("TestName"))
                .andExpect(jsonPath("$.username").value("TestRoman"))
                .andExpect(jsonPath("$.street").value("Teststreet"))
                .andExpect(jsonPath("$.houseNumber").value("45A"))
                .andExpect(jsonPath("$.postalCode").value(12345))
                .andExpect(jsonPath("$.town").value("Testtown"))
                .andExpect(jsonPath("$.country").value("TestCountry"));
    }
    @Test
    void should_find_all_users() throws Exception{
        User existing = new User();
        existing.setId(1L);
        existing.setPrename("Roman");
        existing.setLastname("TestName");
        existing.setUsername("TestRoman");
        existing.setStreet("Teststreet");
        existing.setHouseNumber("45A");
        existing.setPostalCode(12345);
        existing.setTown("Testtown");
        existing.setCountry("TestCountry");

        User existing2 = new User();
        existing2.setId(2L);
        existing2.setPrename("Roman2");
        existing2.setLastname("TestName2");
        existing2.setUsername("TestRoman2");
        existing2.setStreet("Teststreet2");
        existing2.setHouseNumber("45A");
        existing2.setPostalCode(12345);
        existing2.setTown("Testtown2");
        existing2.setCountry("TestCountry2");
        // Mock service
        when(userService.findAll())
                .thenReturn(List.of(existing, existing2));


        // Act + Assert
        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void should_delete_a_user() throws Exception{
        User user = new User();
        user.setId(1L);
        doNothing().when(userService).deleteUser(1L);
        //act and assert
        mockMvc.perform(delete("/user/{userId}", 1L))
                .andExpect(status().is(204));
        verify(userService, times(1)).deleteUser(eq(1L));

    }
}
