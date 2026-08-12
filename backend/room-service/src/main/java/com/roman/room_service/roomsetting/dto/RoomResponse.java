package com.roman.room_service.roomsetting.dto;
import com.roman.room_service.enums.RoomType;

import java.time.LocalDateTime;
import java.math.BigDecimal;
public record RoomResponse(
        Long roomId,
        RoomType roomType,
        Boolean isAvailable,
        String description,
        BigDecimal pricePerNight,
        Integer roomNumber,
        LocalDateTime createdAt
) {}
