package com.roman.user_service.user.service;

import com.roman.user_service.events.EventPublisher;
import com.roman.user_service.user.dto.UserRequest;
import com.roman.user_service.user.model.User;
import com.roman.user_service.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final EventPublisher eventPublisher = mock(EventPublisher.class);
    private final UserService userService = new UserService(
            userRepository,
            passwordEncoder,
            eventPublisher);
    @Test
    void should_update_a_User(){
        //I need first an existing to update this
        User existing = new User();
        existing.setId(1L);
        existing.setPrename("Roman");
        existing.setLastname("Testname");
        existing.setUsername("TestRoman");
        existing.setStreet("Teststreet");
        existing.setHouseNumber("45A");
        existing.setPostalCode(12345);
        existing.setTown("Testtown");
        existing.setCountry("TestCountry");
        //I call the find method
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        //request
        UserRequest userRequest = new UserRequest();
        userRequest.prename = "Roman";
        userRequest.lastname = "Testname";
        userRequest.username = "TestRoman";
        userRequest.street = "Teststreet";
        userRequest.houseNumber = "45A";
        userRequest.postalCode = 12345;
        userRequest.town = "Testtown";
        userRequest.country = "TestCountry";

        //I call the save
        when(userRepository.save(any()))
                .thenAnswer(invoke->invoke.getArgument(0));
        //I use the update method to act
        User result = userService.updateUser(1L, userRequest);
        //and I assert that the update runs
        assertNotNull(result);
        assertEquals("Roman", result.getPrename());
        assertEquals("Testname", result.getLastname());
        assertEquals("TestRoman", result.getUsername());
        assertEquals("Teststreet", result.getStreet());
        assertEquals("45A", result.getHouseNumber());
        assertEquals(12345, result.getPostalCode());
        assertEquals("Testtown", result.getTown());
        assertEquals("TestCountry", result.getCountry());

        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void should_delete_the_user(){
        //I need first an existing to delete this
        User existing = new User();
        existing.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        userService.deleteUser(existing.getId());
        verify(userRepository).delete(existing);
    }
}
