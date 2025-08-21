package com.sportygroup.liveevents.in.scheduler;

import com.sportygroup.liveevents.application.usecase.ProcessLiveEventsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PollingSchedulerAdapterTest {

    @Mock
    private ProcessLiveEventsUseCase processLiveEventsUseCase;

    private PollingSchedulerAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new PollingSchedulerAdapter(processLiveEventsUseCase);
    }

    @Test
    void shouldExecuteProcessLiveEventsUseCase() {
        adapter.pollLiveEvents();

        verify(processLiveEventsUseCase).execute();
    }
}