package com.sportygroup.liveevents.application.usecase;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;
import java.util.Optional;
import java.util.Set;

public class ProcessLiveEventsUseCase {
    private final DomainEventTrackingService trackingService;
    private final ScoreFetcher scoreFetcher;
    private final EventPublisherPort eventPublisher;

    public ProcessLiveEventsUseCase(DomainEventTrackingService trackingService, 
                                   ScoreFetcher scoreFetcher, 
                                   EventPublisherPort eventPublisher) {
        this.trackingService = trackingService;
        this.scoreFetcher = scoreFetcher;
        this.eventPublisher = eventPublisher;
    }

    public void execute() {
        Set<EventId> liveEventIds = trackingService.getLiveEventIds();
        liveEventIds.forEach(this::processEvent);
    }

    private void processEvent(EventId eventId) {
        Optional<Score> score = scoreFetcher.fetchScore(eventId);
        score.ifPresent(s -> eventPublisher.publish(eventId, s));
    }
}