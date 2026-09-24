package com.roman.booking_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.roman.events.RoomAvailabilityEvent;
@Slf4j
@Component
public class RoomEventListener {

    @KafkaListener(topics = "room-events", groupId = "booking-service-group")
    public void handleRoomAvailabilityChanged(RoomAvailabilityEvent event) {
        // optional: logging
        log.info("Room {} availability changed to {}", event.roomNumber(), event.isAvailable());
    }

}