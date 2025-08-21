package com.sportygroup.liveevents.out.external;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestScoreFetcherTest {

    @Mock
    private RestTemplate restTemplate;

    private RestScoreFetcher fetcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fetcher = new RestScoreFetcher(restTemplate, "http://localhost:8081/api/events");
    }

    @Test
    void shouldFetchScore() {
        EventId eventId = new EventId("match-123");
        ScoreResponse response = new ScoreResponse("match-123", "Team A 2-1 Team B");
        
        when(restTemplate.getForObject(anyString(), eq(ScoreResponse.class)))
            .thenReturn(response);

        Optional<Score> result = fetcher.fetchScore(eventId);

        assertTrue(result.isPresent());
        assertEquals("Team A 2-1 Team B", result.get().value());
    }

    @Test
    void shouldReturnEmptyOnException() {
        EventId eventId = new EventId("match-123");
        
        when(restTemplate.getForObject(anyString(), eq(ScoreResponse.class)))
            .thenThrow(new RuntimeException("Connection failed"));

        Optional<Score> result = fetcher.fetchScore(eventId);

        assertFalse(result.isPresent());
    }
}