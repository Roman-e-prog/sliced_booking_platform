package com.roman.price_service.pricesetting.dto;

import com.roman.price_service.enums.BookingType;
import com.roman.price_service.enums.RoomType;

import java.math.BigDecimal;

public class PriceRequest {

    private RoomType roomType;
    private BookingType bookingType;
    private BigDecimal nettoPrice;
    private BigDecimal taxRate;

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public BigDecimal getNettoPrice() {
        return nettoPrice;
    }

    public void setNettoPrice(BigDecimal nettoPrice) {
        this.nettoPrice = nettoPrice;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}


