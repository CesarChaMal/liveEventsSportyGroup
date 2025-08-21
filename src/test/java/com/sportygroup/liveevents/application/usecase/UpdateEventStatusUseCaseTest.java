package com.sportygroup.liveevents.application.usecase;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class UpdateEventStatusUseCaseTest {

    @Mock
    private DomainEventTrackingService trackingService;

    private UpdateEventStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new UpdateEventStatusUseCase(trackingService);
    }

    @Test
    void shouldUpdateEventStatusToLive() {
        useCase.execute("match-123", "live");

        verify(trackingService).updateEventStatus(
            new EventId("match-123"), 
            EventStatus.LIVE
        );
    }

    @Test
    void shouldUpdateEventStatusToEnded() {
        useCase.execute("match-123", "ended");

        verify(trackingService).updateEventStatus(
            new EventId("match-123"), 
            EventStatus.ENDED
        );
    }
}