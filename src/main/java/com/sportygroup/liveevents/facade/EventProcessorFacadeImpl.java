package com.sportygroup.liveevents.facade;

import com.sportygroup.liveevents.model.ScoreResponse;
import com.sportygroup.liveevents.service.fetcher.EventFetcher;
import com.sportygroup.liveevents.service.publisher.EventPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EventProcessorFacadeImpl implements EventProcessorFacade {

    private static final Logger log = LoggerFactory.getLogger(EventProcessorFacadeImpl.class);
    private final EventFetcher eventFetcher;
    private final EventPublisher eventPublisher;

    public EventProcessorFacadeImpl(EventFetcher eventFetcher, EventPublisher eventPublisher) {
        this.eventFetcher = eventFetcher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @CircuitBreaker(name = "scoreService", fallbackMethod = "fallbackProcessEvent")
    public void processEvent(String eventId) {
        Optional<ScoreResponse> score = eventFetcher.fetchScore(eventId);
        score.map(ScoreResponse::currentScore)
                .ifPresent(currentScore -> eventPublisher.publish(eventId, currentScore));
    }

    public void fallbackProcessEvent(String eventId, Throwable t) {
        log.warn("⚠️ Fallback triggered for event {} due to: {}", eventId, t.getMessage());
        // Optional: Store fallback state or notify
    }
}
