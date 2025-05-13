package com.sportygroup.liveevents.service;

import com.sportygroup.liveevents.facade.EventProcessorFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

class PollingSchedulerServiceTest {

    private PollingSchedulerService scheduler;
    private EventTrackingService trackingService;
    private EventProcessorFacade processorFacade;

    @BeforeEach
    void setup() {
        trackingService = mock(EventTrackingService.class);
        processorFacade = mock(EventProcessorFacade.class);
        scheduler = new PollingSchedulerService(trackingService, processorFacade);
    }

    @Test
    void pollLiveEvents_shouldCallProcessorForEachEvent() {
        when(trackingService.getLiveEventIds()).thenReturn(Set.of("match-1", "match-2"));

        scheduler.pollLiveEvents();

        verify(processorFacade).processEvent("match-1");
        verify(processorFacade).processEvent("match-2");
        verifyNoMoreInteractions(processorFacade);
    }
}
