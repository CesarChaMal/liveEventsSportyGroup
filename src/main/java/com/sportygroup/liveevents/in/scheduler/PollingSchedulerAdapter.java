package com.sportygroup.liveevents.in.scheduler;

import com.sportygroup.liveevents.application.usecase.ProcessLiveEventsUseCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PollingSchedulerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(PollingSchedulerAdapter.class);
    private final ProcessLiveEventsUseCase processLiveEventsUseCase;

    public PollingSchedulerAdapter(ProcessLiveEventsUseCase processLiveEventsUseCase) {
        this.processLiveEventsUseCase = processLiveEventsUseCase;
    }

    @Scheduled(fixedDelayString = "${polling.interval:10000}")
    public void pollLiveEvents() {
        logger.debug("Polling live events...");
        processLiveEventsUseCase.execute();
    }
}