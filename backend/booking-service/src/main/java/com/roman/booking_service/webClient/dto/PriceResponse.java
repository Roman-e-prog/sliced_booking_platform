package com.roman.booking_service.webClient.dto;

import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;

import java.math.BigDecimal;

public record PriceResponse(
        Long priceId,
        RoomType roomType,
        BookingType bookingType,
        BigDecimal nettoPrice,
        BigDecimal taxRate
) { }
