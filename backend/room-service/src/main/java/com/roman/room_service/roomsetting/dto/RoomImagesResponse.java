package com.roman.room_service.roomsetting.dto;

import java.time.LocalDateTime;

public record RoomImagesResponse(
        Long imageId,
        Long roomId,
        String alt,
        String title,
        String path,
        LocalDateTime createdAt
) { }
