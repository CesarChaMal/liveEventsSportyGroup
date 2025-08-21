package com.sportygroup.liveevents.application.usecase;

import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;

public class UpdateEventStatusUseCase {
    private final DomainEventTrackingService trackingService;

    public UpdateEventStatusUseCase(DomainEventTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public void execute(String eventId, String status) {
        EventId id = new EventId(eventId);
        EventStatus eventStatus = EventStatus.fromString(status);
        trackingService.updateEventStatus(id, eventStatus);
    }
}