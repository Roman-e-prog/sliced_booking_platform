package com.roman.booking_service.webClient.dto;

import com.roman.booking_service.enums.RoomType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RoomResponse(
        Long roomId,
        RoomType roomType,
        Boolean isAvailable,
        String description,
        BigDecimal pricePerNight,
        Integer roomNumber,
        LocalDateTime createdAt
) {}
