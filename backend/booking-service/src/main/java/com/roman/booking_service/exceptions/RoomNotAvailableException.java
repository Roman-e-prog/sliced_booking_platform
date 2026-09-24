package com.roman.booking_service.exceptions;

public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(Integer roomNumber) {
        super("Room " + roomNumber + " is not available");
    }
}
