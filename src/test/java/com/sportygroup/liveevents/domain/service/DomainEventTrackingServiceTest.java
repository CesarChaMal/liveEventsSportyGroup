package com.sportygroup.liveevents.domain.service;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DomainEventTrackingServiceTest {

    @Mock
    private EventRepository eventRepository;

    private DomainEventTrackingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DomainEventTrackingService(eventRepository);
    }

    @Test
    void shouldSaveLiveEvent() {
        EventId eventId = new EventId("match-123");
        
        service.updateEventStatus(eventId, EventStatus.LIVE);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldRemoveEndedEvent() {
        EventId eventId = new EventId("match-123");
        
        service.updateEventStatus(eventId, EventStatus.ENDED);

        verify(eventRepository).remove(eventId);
    }

    @Test
    void shouldReturnLiveEventIds() {
        Set<EventId> expectedIds = Set.of(new EventId("match-1"), new EventId("match-2"));
        when(eventRepository.findLiveEventIds()).thenReturn(expectedIds);

        Set<EventId> result = service.getLiveEventIds();

        assertEquals(expectedIds, result);
    }
}