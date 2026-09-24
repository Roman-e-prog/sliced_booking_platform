package com.roman.room_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.roman.events.RoomAvailabilityEvent;
import jakarta.annotation.PostConstruct;
@Component
public class RoomEventPublisher {

    private final KafkaTemplate<String, RoomAvailabilityEvent> kafka;

    public RoomEventPublisher(KafkaTemplate<String, RoomAvailabilityEvent> kafka) {
        this.kafka = kafka;
    }
    @PostConstruct
    public void warmUpKafka() {
        // Dummy-Event, damit Producer initialisiert wird
        kafka.send("room-events", new RoomAvailabilityEvent(0, true));
    }
    public void publish(RoomAvailabilityEvent event) {
        kafka.send("room-events", event);
    }
}