package com.roman.user_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.roman.events.UserDeletedEvent;
import jakarta.annotation.PostConstruct;
@Component
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafka;

    public EventPublisher(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }
    @PostConstruct
    public void warmUpKafka() {
        // Dummy-Event, damit Producer initialisiert wird
        kafka.send("user-events", new UserDeletedEvent(0L));
    }

    public void publish(UserDeletedEvent event) {
        kafka.send("user-events", event);
    }
}

