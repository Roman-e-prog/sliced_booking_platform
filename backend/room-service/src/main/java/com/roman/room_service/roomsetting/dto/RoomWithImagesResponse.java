package com.roman.room_service.roomsetting.dto;

import java.util.List;

public record RoomWithImagesResponse(
        RoomResponse room,
        List<RoomImagesResponse> images
) {}
