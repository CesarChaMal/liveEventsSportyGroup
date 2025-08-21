package com.sportygroup.liveevents.infrastructure.adapter;

import com.sportygroup.liveevents.application.usecase.ProcessLiveEventsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PollingSchedulerServiceTest {

    @Mock
    private ProcessLiveEventsUseCase processLiveEventsUseCase;

    private PollingSchedulerService scheduler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        scheduler = new PollingSchedulerService(processLiveEventsUseCase);
    }

    @Test
    void pollLiveEvents_shouldExecuteUseCase() {
        scheduler.pollLiveEvents();

        verify(processLiveEventsUseCase).execute();
    }
}
