package com.roman.booking_service.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.booking_service.booking.dto.BookingRequest;
import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.booking.service.BookingService;
import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.enums.UserType;
import com.roman.booking_service.webClient.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------
    // CREATE BOOKING (POST) – allowed for normal users
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "roman", roles = {"USER"})
    void should_create_booking() throws Exception {

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setNumberOfPersons(2);
        booking.setStartDate(LocalDate.parse("2026-03-10"));
        booking.setEndDate(LocalDate.parse("2026-03-20"));
        booking.setUserType(UserType.BUSINESS_GUEST);
        booking.setBookingType(BookingType.WITH_BREAKFAST);
        booking.setPricePerNight(BigDecimal.valueOf(250));
        booking.setFullPrice(BigDecimal.valueOf(2500));
        booking.setTax(BigDecimal.valueOf(475));
        booking.setBruttoPrice(BigDecimal.valueOf(2975));
        booking.setRoomType(RoomType.ONE_BED);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUserId(1L);

        when(bookingService.bookRoom(any(BookingRequest.class))).thenReturn(booking);

        BookingRequest request = new BookingRequest();
        request.setRoomNumber(101);
        request.setStartDate("2026-03-10");
        request.setEndDate("2026-03-20");
        request.setNumberOfPersons(2);
        request.setBookingType(BookingType.WITH_BREAKFAST);
        request.setUserType(UserType.BUSINESS_GUEST);
        request.setRoomType(RoomType.ONE_BED);

        mockMvc.perform(
                post("/api/booking/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.numberOfPersons").value(2))
                .andExpect(jsonPath("$.startDate").value("2026-03-10"))
                .andExpect(jsonPath("$.endDate").value("2026-03-20"))
                .andExpect(jsonPath("$.userType").value("BUSINESS_GUEST"))
                .andExpect(jsonPath("$.bookingType").value("WITH_BREAKFAST"))
                .andExpect(jsonPath("$.pricePerNight").value(250))
                .andExpect(jsonPath("$.fullPrice").value(2500))
                .andExpect(jsonPath("$.tax").value(475))
                .andExpect(jsonPath("$.bruttoPrice").value(2975))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"));
    }

    // ---------------------------------------------------------
    // GET ONE BOOKING – public
    // ---------------------------------------------------------
    @Test
    void should_get_booking() throws Exception {

        BookingResponse response = new BookingResponse(
                1L,
                2,
                "2026-03-10",
                "2026-03-20",
                UserType.PRIVATE_GUEST,
                BookingType.ONLY_REST,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(380),
                BigDecimal.valueOf(2380),
                RoomType.ONE_BED,
                LocalDateTime.now(),
                null
        );

        when(bookingService.findByIdOrThrow(1L)).thenReturn(response);

        mockMvc.perform(get("/api/booking/{bookingId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.numberOfPersons").value(2))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"));
    }

    // ---------------------------------------------------------
    // GET ALL BOOKINGS – ADMIN only
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void should_get_all_bookings() throws Exception {

        BookingResponse r1 = new BookingResponse(
                1L, 2, "2026-03-10", "2026-03-20",
                UserType.PRIVATE_GUEST, BookingType.ONLY_REST,
                BigDecimal.valueOf(200), BigDecimal.valueOf(2000),
                BigDecimal.valueOf(380), BigDecimal.valueOf(2380),
                RoomType.ONE_BED, LocalDateTime.now(), null
        );

        BookingResponse r2 = new BookingResponse(
                2L, 1, "2026-04-01", "2026-04-05",
                UserType.BUSINESS_GUEST, BookingType.WITH_BREAKFAST,
                BigDecimal.valueOf(250), BigDecimal.valueOf(1000),
                BigDecimal.valueOf(190), BigDecimal.valueOf(1190),
                RoomType.TWO_BED, LocalDateTime.now(), null
        );

        when(bookingService.getAllBookings()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/booking/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ---------------------------------------------------------
    // UPDATE BOOKING – allowed for normal users
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "roman", roles = {"USER"})
    void should_update_booking() throws Exception {

        Booking updated = new Booking();
        updated.setBookingId(1L);
        updated.setNumberOfPersons(3);
        updated.setStartDate(LocalDate.parse("2026-03-15"));
        updated.setEndDate(LocalDate.parse("2026-03-25"));
        updated.setUserType(UserType.BUSINESS_GUEST);
        updated.setBookingType(BookingType.WITH_BREAKFAST);
        updated.setPricePerNight(BigDecimal.valueOf(250));
        updated.setFullPrice(BigDecimal.valueOf(2500));
        updated.setTax(BigDecimal.valueOf(475));
        updated.setBruttoPrice(BigDecimal.valueOf(2975));
        updated.setRoomType(RoomType.ONE_BED);
        updated.setCreatedAt(LocalDateTime.now());
        updated.setUserId(1L);

        when(bookingService.updateBooking(eq(1L), any(BookingRequest.class))).thenReturn(updated);

        BookingRequest request = new BookingRequest();
        request.setRoomNumber(101);
        request.setNumberOfPersons(3);
        request.setStartDate("2026-03-15");
        request.setEndDate("2026-03-25");

        mockMvc.perform(
                put("/api/booking/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.numberOfPersons").value(3))
                .andExpect(jsonPath("$.roomType").value("ONE_BED"));
    }

    // ---------------------------------------------------------
    // DELETE BOOKING – ADMIN only
    // ---------------------------------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void should_delete_booking() throws Exception {

        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/booking/{bookingId}", 1L))
                .andExpect(status().isNoContent());
    }
}
