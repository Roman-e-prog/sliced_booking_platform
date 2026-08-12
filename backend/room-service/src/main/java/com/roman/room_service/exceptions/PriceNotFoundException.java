package com.roman.room_service.exceptions;


import com.roman.room_service.enums.BookingType;
import com.roman.room_service.enums.RoomType;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(RoomType roomType, BookingType bookingType) {
        super("No price configured for roomType=" + roomType + " and bookingType=" + bookingType);
    }
}
