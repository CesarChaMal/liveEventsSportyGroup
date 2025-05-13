package com.sportygroup.liveevents.facade;

import com.sportygroup.liveevents.model.ScoreResponse;
import com.sportygroup.liveevents.service.fetcher.EventFetcher;
import com.sportygroup.liveevents.service.publisher.EventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventProcessorFacadeImpl implements EventProcessorFacade {

    private final EventFetcher eventFetcher;
    private final EventPublisher eventPublisher;

    public EventProcessorFacadeImpl(EventFetcher eventFetcher, EventPublisher eventPublisher) {
        this.eventFetcher = eventFetcher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void processEvent(String eventId) {
        Optional<ScoreResponse> score = eventFetcher.fetchScore(eventId);
        score.map(ScoreResponse::currentScore)
                .ifPresent(currentScore -> eventPublisher.publish(eventId, currentScore));
    }
}
