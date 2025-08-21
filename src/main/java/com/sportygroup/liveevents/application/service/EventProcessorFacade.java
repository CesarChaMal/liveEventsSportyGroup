package com.sportygroup.liveevents.application.service;

public interface EventProcessorFacade {
    void processEvent(String eventId);
}