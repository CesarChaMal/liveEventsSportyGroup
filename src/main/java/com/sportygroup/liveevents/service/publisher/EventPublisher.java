package com.sportygroup.liveevents.service.publisher;

public interface EventPublisher {
    void publish(String eventId, String payload);
}
