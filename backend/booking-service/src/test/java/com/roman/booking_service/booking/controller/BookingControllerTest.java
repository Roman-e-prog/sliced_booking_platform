package com.roman.booking_service.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.booking_service.booking.dto.BookingRequest;
import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.user.dto.UserResponse;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.booking.repository.BookingRepository;
import com.roman.booking_service.booking.service.BookingService;
import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.pricesetting.repository.PriceRepository;
import com.roman.booking_service.security.jwt.JwtService;
import com.roman.booking_service.user.model.User;
import com.roman.booking_service.user.repository.UserRepository;
import com.roman.booking_service.enums.UserType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private BookingService bookingService;
    @MockitoBean private BookingRepository bookingRepository;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private PriceRepository priceRepository;
    @MockitoBean private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
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
        );
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getNumberOfPersons(),
                booking.getStartDate().toString(),
                booking.getEndDate().toString(),
                booking.getUserType(),
                booking.getBookingType(),
                booking.getPricePerNight(),
                booking.getFullPrice(),
                booking.getTax(),
                booking.getBruttoPrice(),
                booking.getRoomType(),
                booking.getCreatedAt(),
                toUserResponse(booking.getUser())
        );
    }

    @Test
    void should_find_all_bookings() throws Exception {

        // Booking 1
        Booking booking1 = new Booking();
        booking1.setBookingId(1L);
        booking1.setStartDate(LocalDate.parse("2026-01-03"));
        booking1.setEndDate(LocalDate.parse("2026-01-14"));
        booking1.setNumberOfPersons(1);
        booking1.setBookingType(BookingType.WITH_BREAKFAST);
        booking1.setUserType(UserType.PRIVATE_GUEST);
        booking1.setRoomType(RoomType.ONE_BED);
        booking1.setPricePerNight(BigDecimal.valueOf(100));
        booking1.setFullPrice(BigDecimal.valueOf(1100));
        booking1.setTax(BigDecimal.valueOf(209));
        booking1.setBruttoPrice(BigDecimal.valueOf(1309));
        booking1.setCreatedAt(LocalDateTime.now());

        User user1 = new User();
        user1.setId(1L);
        user1.setPrename("Roman");
        user1.setLastname("Rostock");
        user1.setEmail("roman@example.com");
        user1.setCreatedAt(LocalDateTime.now());
        booking1.setUser(user1);

        BookingResponse bookingResponse1 = toBookingResponse(booking1);

        // Booking 2
        Booking booking2 = new Booking();
        booking2.setBookingId(2L);
        booking2.setStartDate(LocalDate.parse("2026-01-03"));
        booking2.setEndDate(LocalDate.parse("2026-01-14"));
        booking2.setNumberOfPersons(1);
        booking2.setBookingType(BookingType.WITH_BREAKFAST);
        booking2.setUserType(UserType.PRIVATE_GUEST);
        booking2.setRoomType(RoomType.ONE_BED);
        booking2.setPricePerNight(BigDecimal.valueOf(100));
        booking2.setFullPrice(BigDecimal.valueOf(1100));
        booking2.setTax(BigDecimal.valueOf(209));
        booking2.setBruttoPrice(BigDecimal.valueOf(1309));
        booking2.setCreatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setPrename("Max");
        user2.setLastname("Mustermann");
        user2.setEmail("max@example.com");
        user2.setCreatedAt(LocalDateTime.now());
        booking2.setUser(user2);

        BookingResponse bookingResponse2 = toBookingResponse(booking2);

        when(bookingService.getAllBookings()).thenReturn(List.of(bookingResponse1, bookingResponse2));

        mockMvc.perform(get("/booking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
