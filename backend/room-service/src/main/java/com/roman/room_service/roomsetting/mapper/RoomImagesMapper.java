package com.roman.room_service.roomsetting.mapper;
import com.roman.room_service.roomsetting.dto.RoomImagesResponse;
import com.roman.room_service.roomsetting.model.RoomImages;

import java.util.List;

public class RoomImagesMapper {

    public static RoomImagesResponse toResponse(RoomImages img) {
        return new RoomImagesResponse(
                img.getImageId(),
                img.getRoom().getRoomId(),
                img.getAlt(),
                img.getTitle(),
                img.getPath(),
                img.getCreatedAt()
        );
    }

    public static List<RoomImagesResponse> toResponseList(List<RoomImages> images) {
        return images.stream()
                .map(RoomImagesMapper::toResponse)
                .toList();
    }
}
