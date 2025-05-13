package com.sportygroup.liveevents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventTrackingServiceTest {

    private EventTrackingService trackingService;

    @BeforeEach
    void setUp() {
        trackingService = new EventTrackingService();
    }

    @Test
    void testUpdateEventStatus_live_shouldAddEventId() {
        trackingService.updateEventStatus("match-1", "live");
        assertTrue(trackingService.getLiveEventIds().contains("match-1"));
    }

    @Test
    void testUpdateEventStatus_notLive_shouldRemoveEventId() {
        trackingService.updateEventStatus("match-1", "live");
        trackingService.updateEventStatus("match-1", "ended");
        assertFalse(trackingService.getLiveEventIds().contains("match-1"));
    }

    @Test
    void testGetLiveEventIds_shouldReturnUnmodifiableSet() {
        trackingService.updateEventStatus("match-2", "live");
        Set<String> ids = trackingService.getLiveEventIds();
        assertEquals(1, ids.size());
        assertThrows(UnsupportedOperationException.class, () -> ids.add("illegal"));
    }
}
