package com.sportygroup.liveevents.application.usecase;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class ProcessLiveEventsUseCaseTest {

    @Mock
    private DomainEventTrackingService trackingService;
    @Mock
    private ScoreFetcher scoreFetcher;
    @Mock
    private EventPublisherPort eventPublisher;

    private ProcessLiveEventsUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ProcessLiveEventsUseCase(trackingService, scoreFetcher, eventPublisher);
    }

    @Test
    void shouldProcessLiveEventsWithScores() {
        EventId eventId = new EventId("match-123");
        Score score = new Score("Team A 2-1 Team B");
        
        when(trackingService.getLiveEventIds()).thenReturn(Set.of(eventId));
        when(scoreFetcher.fetchScore(eventId)).thenReturn(Optional.of(score));

        useCase.execute();

        verify(eventPublisher).publish(eventId, score);
    }

    @Test
    void shouldSkipEventsWithoutScores() {
        EventId eventId = new EventId("match-123");
        
        when(trackingService.getLiveEventIds()).thenReturn(Set.of(eventId));
        when(scoreFetcher.fetchScore(eventId)).thenReturn(Optional.empty());

        useCase.execute();

        verify(eventPublisher, never()).publish(any(), any());
    }
}