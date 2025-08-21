package com.sportygroup.liveevents.domain.service;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.EventRepository;

import java.util.Set;

public class DomainEventTrackingService {
    private final EventRepository eventRepository;

    public DomainEventTrackingService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void updateEventStatus(EventId eventId, EventStatus status) {
        Event event = new Event(eventId, status);
        if (event.isLive()) {
            eventRepository.save(event);
        } else {
            eventRepository.remove(eventId);
        }
    }

    public Set<EventId> getLiveEventIds() {
        return eventRepository.findLiveEventIds();
    }
}