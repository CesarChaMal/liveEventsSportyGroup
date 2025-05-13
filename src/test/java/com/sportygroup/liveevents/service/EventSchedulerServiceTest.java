package com.sportygroup.liveevents.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportygroup.liveevents.model.ScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventSchedulerServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessagePublisherService publisherService;

    @InjectMocks
    private EventSchedulerService schedulerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateEventStatus_live() {
        schedulerService.updateEventStatus("event123", "live");

        Map<String, Boolean> map = schedulerService.getLiveEvents();
        assertTrue(map.containsKey("event123"));
        assertTrue(map.get("event123"));
    }

    @Test
    void testUpdateEventStatus_notLive() {
        schedulerService.updateEventStatus("event456", "live");
        schedulerService.updateEventStatus("event456", "not live");

        Map<String, Boolean> map = schedulerService.getLiveEvents();
        assertFalse(map.containsKey("event456"));
    }

    @Test
    void testPollLiveEvents_shouldCallRestAndPublish() {
        // Given
        String eventId = "match-1";
        schedulerService.updateEventStatus(eventId, "live");

        ScoreResponse mockScore = new ScoreResponse();
        mockScore.setEventId(eventId);
        mockScore.setCurrentScore("Team A 1 - 0 Team B");

        when(restTemplate.getForObject(
                eq("http://localhost:8081/api/events/" + eventId + "/score"),
                eq(ScoreResponse.class)))
                .thenReturn(mockScore);

        // When
        schedulerService.pollLiveEvents();

        // Then
        verify(publisherService, times(1)).publishMessage(eventId, "Team A 1 - 0 Team B");
    }
}
