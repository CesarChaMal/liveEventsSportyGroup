package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.domain.model.*;
import java.util.Set;

public interface EventRepository {
    void save(Event event);
    void remove(EventId eventId);
    Set<EventId> findLiveEventIds();
}