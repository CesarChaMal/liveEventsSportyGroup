package com.sportygroup.liveevents.service.publisher;

import com.sportygroup.liveevents.service.MessagePublisherService;
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
