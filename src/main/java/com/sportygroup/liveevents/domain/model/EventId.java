package com.sportygroup.liveevents.domain.model;

public record EventId(String value) {
    public EventId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
    }
}