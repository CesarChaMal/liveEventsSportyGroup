package com.sportygroup.liveevents.infrastructure.config;

import com.sportygroup.liveevents.application.usecase.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HexagonalConfig {

    // Domain services need manual configuration since @DomainService doesn't extend @Component
    // to keep domain layer framework-independent
    @Bean
    public DomainEventTrackingService domainEventTrackingService(EventRepository eventRepository) {
        return new DomainEventTrackingService(eventRepository);
    }

    // Use cases are now automatically configured via @UseCase annotation
    // ProcessLiveEventsUseCase and UpdateEventStatusUseCase beans created automatically
}