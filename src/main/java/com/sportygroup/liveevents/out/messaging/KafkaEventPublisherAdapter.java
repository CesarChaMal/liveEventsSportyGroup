package com.sportygroup.liveevents.out.messaging;

import com.sportygroup.liveevents.domain.model.EventId;
import com.sportygroup.liveevents.domain.model.Score;
import com.sportygroup.liveevents.domain.port.EventPublisherPort;
import org.springframework.stereotype.Component;

// This class replaces the old KafkaEventPublisher implementation with proper hexagonal architecture
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    private final MessagePublisherService publisherService;

    public KafkaEventPublisherAdapter(MessagePublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Override
    public void publish(EventId eventId, Score score) {
        publisherService.publishMessage(eventId.value(), score.value());
    }
}