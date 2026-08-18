package com.roman.booking_service.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.booking.service.BookingService;
import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.enums.UserType;
import com.roman.booking_service.webClient.client.PriceClient;
import com.roman.booking_service.webClient.client.RoomClient;
import com.roman.booking_service.webClient.client.SecurityClient;
import com.roman.booking_service.webClient.client.UserClient;
import com.roman.booking_service.webClient.dto.UserResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
@Log4j2
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
//lade nicht, die Configuratuion, wenn der Test läuft
@ImportAutoConfiguration(exclude = {
        com.roman.booking_service.webClient.config.PriceClientConfig.class,
        com.roman.booking_service.webClient.config.RoomClientConfig.class,
        com.roman.booking_service.webClient.config.UserClientConfig.class
})
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private BookingService bookingService;
    @MockitoBean private UserClient userClient;
    @MockitoBean private PriceClient priceClient;
    @MockitoBean private SecurityClient securityClient;
    @MockitoBean private RoomClient roomClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_find_all_bookings() throws Exception {

        // Mock UserResponse 1
        UserResponse user1 = new UserResponse(
                1L,
                "Roman",
                "Rostock",
                "romanUser",
                "Street 1",
                "10",
                59821,
                "Arnsberg",
                "Germany",
                "roman@example.com",
                LocalDate.now().minusYears(30),
                "USER",
                LocalDateTime.now()
        );

        // Mock BookingResponse 1
        BookingResponse bookingResponse1 = new BookingResponse(
                1L,
                1,
                "2026-01-03",
                "2026-01-14",
                UserType.PRIVATE_GUEST,
                BookingType.WITH_BREAKFAST,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(1100),
                BigDecimal.valueOf(209),
                BigDecimal.valueOf(1309),
                RoomType.ONE_BED,
                LocalDateTime.now(),
                user1
        );

        // Mock UserResponse 2
        UserResponse user2 = new UserResponse(
                2L,
                "Max",
                "Mustermann",
                "maxUser",
                "Street 2",
                "20",
                59821,
                "Arnsberg",
                "Germany",
                "max@example.com",
                LocalDate.now().minusYears(25),
                "USER",
                LocalDateTime.now()
        );

        // Mock BookingResponse 2
        BookingResponse bookingResponse2 = new BookingResponse(
                2L,
                1,
                "2026-01-03",
                "2026-01-14",
                UserType.PRIVATE_GUEST,
                BookingType.WITH_BREAKFAST,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(1100),
                BigDecimal.valueOf(209),
                BigDecimal.valueOf(1309),
                RoomType.ONE_BED,
                LocalDateTime.now(),
                user2
        );

        when(bookingService.getAllBookings()).thenReturn(List.of(bookingResponse1, bookingResponse2));

        mockMvc.perform(get("/booking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
