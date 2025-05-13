package com.sportygroup.liveevents.service;

import com.sportygroup.liveevents.facade.EventProcessorFacade;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PollingSchedulerService {

    private final EventTrackingService trackingService;
    private final EventProcessorFacade processorFacade;

    public PollingSchedulerService(EventTrackingService trackingService, EventProcessorFacade processorFacade) {
        this.trackingService = trackingService;
        this.processorFacade = processorFacade;
    }

    @Scheduled(fixedRate = 10000)
    public void pollLiveEvents() {
        Set<String> liveEventIds = trackingService.getLiveEventIds();
        liveEventIds.forEach(processorFacade::processEvent);
    }
}
