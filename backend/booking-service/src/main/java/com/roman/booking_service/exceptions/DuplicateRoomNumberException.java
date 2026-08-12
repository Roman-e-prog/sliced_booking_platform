package com.roman.booking_service.exceptions;

public class DuplicateRoomNumberException extends RuntimeException{
    public DuplicateRoomNumberException(Integer roomNumber) {
        super("RoomNumber is already created" + roomNumber);
    }
}
