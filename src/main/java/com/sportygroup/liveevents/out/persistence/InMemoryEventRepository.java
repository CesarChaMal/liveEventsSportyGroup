package com.sportygroup.liveevents.out.persistence;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.EventRepository;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryEventRepository implements EventRepository {
    private final Set<EventId> liveEvents = ConcurrentHashMap.newKeySet();

    @Override
    public void save(Event event) {
        liveEvents.add(event.getEventId());
    }

    @Override
    public void remove(EventId eventId) {
        liveEvents.remove(eventId);
    }

    @Override
    public Set<EventId> findLiveEventIds() {
        return Collections.unmodifiableSet(liveEvents);
    }
}