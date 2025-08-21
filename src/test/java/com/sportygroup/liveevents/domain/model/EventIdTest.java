package com.sportygroup.liveevents.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventIdTest {

    @Test
    void shouldCreateValidEventId() {
        EventId eventId = new EventId("match-123");
        assertEquals("match-123", eventId.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new EventId(null));
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new EventId(""));
        assertThrows(IllegalArgumentException.class, () -> new EventId("   "));
    }
}