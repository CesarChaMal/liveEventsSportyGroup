package com.sportygroup.liveevents.domain.model;

public class Event {
    private final EventId eventId;
    private EventStatus status;

    public Event(EventId eventId, EventStatus status) {
        this.eventId = eventId;
        this.status = status;
    }

    public EventId getEventId() {
        return eventId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void updateStatus(EventStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isLive() {
        return status == EventStatus.LIVE;
    }
}