package com.sportygroup.liveevents.in.web;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class MockScoreWebAdapterTest {

    private MockScoreWebAdapter mockScoreWebAdapter;

    @BeforeEach
    void setUp() {
        mockScoreWebAdapter = new MockScoreWebAdapter();
    }

    @Test
    void shouldReturnMockScoreResponse() {
        String eventId = "test-event-123";
        
        ScoreResponse response = mockScoreWebAdapter.getScore(eventId);
        
        assertNotNull(response);
        assertEquals(eventId, response.eventId());
        assertEquals("Team A 1 - 0 Team B", response.currentScore());
    }
}