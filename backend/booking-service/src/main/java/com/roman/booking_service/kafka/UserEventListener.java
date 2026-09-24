package com.roman.booking_service.kafka;

import com.roman.booking_service.booking.repository.BookingRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.roman.events.UserDeletedEvent;
@Component
public class UserEventListener {

    private final BookingRepository bookingRepository;

    public UserEventListener(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @KafkaListener(topics = "user-events", groupId = "booking-service-group")
    public void handleUserDeleted(UserDeletedEvent event) {
        bookingRepository.deleteByUserId(event.userId());
    }
}
