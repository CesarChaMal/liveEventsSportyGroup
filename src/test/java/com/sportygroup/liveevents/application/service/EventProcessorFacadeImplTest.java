
package com.sportygroup.liveevents.application.service;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.domain.port.ScoreFetcher;
import com.sportygroup.liveevents.domain.port.EventPublisherPort;
import com.sportygroup.liveevents.domain.model.EventId;
import com.sportygroup.liveevents.domain.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class EventProcessorFacadeImplTest {

    @Mock
    private ScoreFetcher scoreFetcher;

    @Mock
    private EventPublisherPort eventPublisher;

    private EventProcessorFacadeImpl eventProcessorFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventProcessorFacade = new EventProcessorFacadeImpl(scoreFetcher, eventPublisher);
    }

    @Test
    void testProcessEvent_shouldFetchAndPublishScore() {
        String eventId = "match-1";
        ScoreResponse mockResponse = new ScoreResponse(eventId, "Team A 1 - 0 Team B");

        when(scoreFetcher.fetchScore(new EventId(eventId))).thenReturn(Optional.of(new Score("Team A 1 - 0 Team B")));

        eventProcessorFacade.processEvent(eventId);

        verify(eventPublisher).publish(new EventId(eventId), new Score("Team A 1 - 0 Team B"));
    }

    @Test
    void testProcessEvent_shouldHandleEmptyResponseGracefully() {
        String eventId = "match-2";
        when(scoreFetcher.fetchScore(new EventId(eventId))).thenReturn(Optional.empty());

        eventProcessorFacade.processEvent(eventId);

        verify(eventPublisher, never()).publish(any(), any());
    }
}