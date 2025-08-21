package com.sportygroup.liveevents.service;

import com.sportygroup.liveevents.facade.EventProcessorFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PollingSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(PollingSchedulerService.class);
    private final EventTrackingService trackingService;
    private final EventProcessorFacade processorFacade;

    public PollingSchedulerService(EventTrackingService trackingService, EventProcessorFacade processorFacade) {
        this.trackingService = trackingService;
        this.processorFacade = processorFacade;
    }

    @Scheduled(fixedDelayString = "${polling.interval:10000}")
    public void pollLiveEvents() {
        Set<String> liveEventIds = trackingService.getLiveEventIds();
        logger.debug("Polling {} live events...", liveEventIds.size());
//        liveEventIds.forEach(eventId -> processorFacade.processEvent(eventId));
        liveEventIds.forEach(this::processEvent);
    }

    private void processEvent(String eventId) {
        logger.debug("Processing event ID: {}", eventId);
        processorFacade.processEvent(eventId);
    }
}
