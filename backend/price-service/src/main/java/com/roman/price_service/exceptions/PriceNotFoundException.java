package com.roman.price_service.exceptions;

import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;

public class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException(RoomType roomType, BookingType bookingType) {
        super("No price configured for roomType=" + roomType + " and bookingType=" + bookingType);
    }
}
