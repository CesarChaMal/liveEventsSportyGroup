
package com.sportygroup.liveevents.facade;

import com.sportygroup.liveevents.model.ScoreResponse;
import com.sportygroup.liveevents.service.fetcher.EventFetcher;
import com.sportygroup.liveevents.service.publisher.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class EventProcessorFacadeImplTest {

    @Mock
    private EventFetcher eventFetcher;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private EventProcessorFacadeImpl eventProcessorFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessEvent_shouldFetchAndPublishScore() {
        String eventId = "match-1";
        ScoreResponse mockResponse = new ScoreResponse(eventId, "Team A 1 - 0 Team B");

        when(eventFetcher.fetchScore(eventId)).thenReturn(Optional.of(mockResponse));

        eventProcessorFacade.processEvent(eventId);

        verify(eventPublisher).publish(eventId, "Team A 1 - 0 Team B");
    }

    @Test
    void testProcessEvent_shouldHandleEmptyResponseGracefully() {
        String eventId = "match-2";
        when(eventFetcher.fetchScore(eventId)).thenReturn(Optional.empty());

        eventProcessorFacade.processEvent(eventId);

        verify(eventPublisher, never()).publish(any(), any());
    }
}