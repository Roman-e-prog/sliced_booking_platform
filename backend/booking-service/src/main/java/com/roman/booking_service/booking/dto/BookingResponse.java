package com.roman.booking_service.booking.dto;

import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.enums.UserType;
import com.roman.booking_service.webClient.dto.UserResponse;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record BookingResponse(
        Long bookingId,
        Integer numberOfPersons,
        String startDate,
        String endDate,
        UserType userType,
        BookingType bookingType,
        BigDecimal pricePerNight,
        BigDecimal fullPrice,
        BigDecimal tax,
        BigDecimal bruttoPrice,
        RoomType roomType,
        LocalDateTime createdAt,
        UserResponse user
) {}
