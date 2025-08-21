package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.domain.model.EventId;
import com.sportygroup.liveevents.domain.model.Score;

public interface EventPublisherPort {
    void publish(EventId eventId, Score score);
}