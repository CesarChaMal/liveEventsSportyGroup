package com.sportygroup.liveevents.domain.model;

public enum EventStatus {
    LIVE, ENDED;

    public static EventStatus fromString(String status) {
        return "live".equalsIgnoreCase(status) ? LIVE : ENDED;
    }
}