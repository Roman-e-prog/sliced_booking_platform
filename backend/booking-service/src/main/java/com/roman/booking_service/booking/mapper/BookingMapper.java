package com.roman.booking_service.booking.mapper;

import com.roman.booking_service.booking.dto.BookingResponse;
import com.roman.booking_service.booking.model.Booking;
import com.roman.booking_service.webClient.client.UserClient;
import com.roman.booking_service.webClient.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    private final UserClient userClient;

    public BookingMapper(UserClient userClient) {
        this.userClient = userClient;
    }

    public BookingResponse toResponse(Booking booking) {

        UserResponse user = userClient.fetchUserById(booking.getUserId());

        return new BookingResponse(
                booking.getBookingId(),
                booking.getNumberOfPersons(),
                booking.getStartDate().toString(),
                booking.getEndDate().toString(),
                booking.getUserType(),
                booking.getBookingType(),
                booking.getPricePerNight(),
                booking.getFullPrice(),
                booking.getTax(),
                booking.getBruttoPrice(),
                booking.getRoomType(),
                booking.getCreatedAt(),
                user
        );
    }
}
