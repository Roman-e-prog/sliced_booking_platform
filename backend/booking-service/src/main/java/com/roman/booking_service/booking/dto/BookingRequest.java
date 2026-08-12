package com.roman.booking_service.booking.dto;

import com.roman.booking_service.enums.BookingType;
import com.roman.booking_service.enums.RoomType;
import com.roman.booking_service.enums.UserType;

import java.math.BigDecimal;

public class BookingRequest {
    private Integer roomNumber;
    private Integer numberOfPersons;
    private String startDate;
    private String endDate;
    private UserType userType;
    private BookingType bookingType;
    private RoomType roomType;
    

    public Integer getRoomNumber() { return roomNumber; }
    public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }


    public Integer getNumberOfPersons() { return numberOfPersons; }
    public void setNumberOfPersons(Integer numberOfPersons) { this.numberOfPersons = numberOfPersons; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public BookingType getBookingType() { return bookingType; }
    public void setBookingType(BookingType bookingType) { this.bookingType = bookingType; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }
}

