package com.sportygroup.liveevents.model;

public class EventStatusRequest {

    private String eventId;
    private String status; // e.g. "live" or "not live"

    public EventStatusRequest() {
        // Default constructor for deserialization
    }

    public EventStatusRequest(String eventId, String status) {
        this.eventId = eventId;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
