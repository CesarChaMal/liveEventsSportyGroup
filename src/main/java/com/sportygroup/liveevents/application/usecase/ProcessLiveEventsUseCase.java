package com.sportygroup.liveevents.application.usecase;

import com.sportygroup.liveevents.common.UseCase;
import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;
import java.util.Optional;
import java.util.Set;

// This use case now also serves as a facade for event processing operations
// It handles both batch processing (execute) and single event processing (processEvent)
// Replaces the need for a separate EventProcessorFacade
@UseCase
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

    // Public method for processing single events (replaces EventProcessorFacadeImpl)
    public void processEvent(EventId eventId) {
        Optional<Score> score = scoreFetcher.fetchScore(eventId);
        score.ifPresent(s -> eventPublisher.publish(eventId, s));
    }

    // Convenience method for string eventId
    public void processEvent(String eventId) {
        processEvent(new EventId(eventId));
    }
}