package com.sportygroup.liveevents.out.messaging;

import com.sportygroup.liveevents.common.ExternalAdapter;
import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.EventPublisherPort;

import com.sportygroup.liveevents.out.messaging.MessagePublisherService;

// This class replaces the old KafkaEventPublisher implementation with proper hexagonal architecture
@ExternalAdapter
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