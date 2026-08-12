package com.roman.price_service.pricesetting.dto;

import java.math.BigDecimal;
import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;

public record PriceResponse (
        Long priceId,
        RoomType roomType,
        BookingType bookingType,
        BigDecimal nettoPrice,
        BigDecimal taxRate
){}
