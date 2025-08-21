package com.sportygroup.liveevents.out.messaging;

import com.sportygroup.liveevents.domain.port.EventPublisher;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private final MessagePublisherService publisherService;

    public KafkaEventPublisher(MessagePublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    public void publish(String eventId, String payload) {
        publisherService.publishMessage(eventId, payload);
    }
}
