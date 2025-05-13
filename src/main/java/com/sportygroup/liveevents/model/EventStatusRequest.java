package com.sportygroup.liveevents.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

public final class EventStatusRequest {
    @NotBlank
    private final String eventId;

    @Pattern(regexp = "live|not live")
    private final String status;

    public EventStatusRequest(String eventId, String status) {
        this.eventId = eventId;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventStatusRequest)) return false;
        EventStatusRequest that = (EventStatusRequest) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, status);
    }

    @Override
    public String toString() {
        return "EventStatusRequest{" +
                "eventId='" + eventId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
