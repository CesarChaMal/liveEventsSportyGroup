package com.sportygroup.liveevents.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventStatusTest {

    @Test
    void shouldCreateLiveStatus() {
        EventStatus status = EventStatus.fromString("live");
        assertEquals(EventStatus.LIVE, status);
    }

    @Test
    void shouldCreateEndedStatus() {
        EventStatus status = EventStatus.fromString("ended");
        assertEquals(EventStatus.ENDED, status);
    }

    @Test
    void shouldHandleCaseInsensitive() {
        assertEquals(EventStatus.LIVE, EventStatus.fromString("LIVE"));
        assertEquals(EventStatus.LIVE, EventStatus.fromString("Live"));
    }

    @Test
    void shouldDefaultToEndedForUnknownStatus() {
        assertEquals(EventStatus.ENDED, EventStatus.fromString("unknown"));
        assertEquals(EventStatus.ENDED, EventStatus.fromString(""));
    }
}