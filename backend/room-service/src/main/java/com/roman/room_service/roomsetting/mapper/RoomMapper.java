package com.roman.room_service.roomsetting.mapper;

import com.roman.room_service.roomsetting.dto.RoomResponse;
import com.roman.room_service.roomsetting.model.Room;

import java.util.List;

public class RoomMapper {
    public static RoomResponse toResponse(Room room){
        return new RoomResponse(
                room.getRoomId(),
                room.getRoomType(),
                room.getIsAvailable(),
                room.getDescription(),
                room.getPricePerNight(),
                room.getRoomNumber(),
                room.getCreatedAt()
        );
    }
}
