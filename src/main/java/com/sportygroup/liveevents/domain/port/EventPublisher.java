package com.sportygroup.liveevents.domain.port;

public interface EventPublisher {
    void publish(String eventId, String payload);
}
