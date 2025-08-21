package com.sportygroup.liveevents.out.persistence;

import com.sportygroup.liveevents.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEventRepositoryTest {

    private InMemoryEventRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEventRepository();
    }

    @Test
    void shouldSaveEvent() {
        EventId eventId = new EventId("match-123");
        Event event = new Event(eventId, EventStatus.LIVE);

        repository.save(event);

        Set<EventId> liveEvents = repository.findLiveEventIds();
        assertTrue(liveEvents.contains(eventId));
    }

    @Test
    void shouldRemoveEvent() {
        EventId eventId = new EventId("match-123");
        Event event = new Event(eventId, EventStatus.LIVE);
        repository.save(event);

        repository.remove(eventId);

        Set<EventId> liveEvents = repository.findLiveEventIds();
        assertFalse(liveEvents.contains(eventId));
    }

    @Test
    void shouldReturnUnmodifiableSet() {
        Set<EventId> liveEvents = repository.findLiveEventIds();
        
        assertThrows(UnsupportedOperationException.class, () -> 
            liveEvents.add(new EventId("test"))
        );
    }
}