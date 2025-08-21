package com.sportygroup.liveevents.infrastructure.config;

import com.sportygroup.liveevents.application.usecase.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HexagonalConfig {

    @Bean
    public DomainEventTrackingService domainEventTrackingService(EventRepository eventRepository) {
        return new DomainEventTrackingService(eventRepository);
    }

    @Bean
    public UpdateEventStatusUseCase updateEventStatusUseCase(DomainEventTrackingService trackingService) {
        return new UpdateEventStatusUseCase(trackingService);
    }

    @Bean
    public ProcessLiveEventsUseCase processLiveEventsUseCase(DomainEventTrackingService trackingService,
                                                           ScoreFetcher scoreFetcher,
                                                           EventPublisherPort eventPublisher) {
        return new ProcessLiveEventsUseCase(trackingService, scoreFetcher, eventPublisher);
    }
}