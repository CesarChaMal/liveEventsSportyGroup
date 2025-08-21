package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.domain.model.EventId;
import com.sportygroup.liveevents.domain.model.Score;

// This interface replaces the old EventPublisher interface with proper hexagonal architecture
// using domain value objects instead of primitive strings
public interface EventPublisherPort {
    void publish(EventId eventId, Score score);
}