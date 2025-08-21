package com.sportygroup.liveevents.application.service;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventProcessorFacadeImpl implements EventProcessorFacade {

    private final ScoreFetcher scoreFetcher;
    private final EventPublisherPort eventPublisher;

    public EventProcessorFacadeImpl(ScoreFetcher scoreFetcher, EventPublisherPort eventPublisher) {
        this.scoreFetcher = scoreFetcher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processEvent(String eventId) {
        EventId id = new EventId(eventId);
        Optional<Score> score = scoreFetcher.fetchScore(id);
        score.ifPresent(s -> eventPublisher.publish(id, s));
    }
}
