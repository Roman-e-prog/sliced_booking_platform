package com.roman.room_service.exceptions;

public class RoomNumberNotFoundException extends RuntimeException {
    public RoomNumberNotFoundException(Integer roomNumber) {
        super("No Room with this number" + roomNumber);
    }
}

