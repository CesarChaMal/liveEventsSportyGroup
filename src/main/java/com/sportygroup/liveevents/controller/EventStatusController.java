package com.sportygroup.liveevents.controller;

import com.sportygroup.liveevents.model.EventStatusRequest;
import com.sportygroup.liveevents.service.EventSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventStatusController {

    private static final Logger logger = LoggerFactory.getLogger(EventStatusController.class);
    private final EventSchedulerService schedulerService;

    public EventStatusController(EventSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/status")
    public ResponseEntity<String> updateEventStatus(@RequestBody EventStatusRequest request) {
        logger.info("Received status update for event {}: {}", request.getEventId(), request.getStatus());
        schedulerService.updateEventStatus(request.getEventId(), request.getStatus());
        return ResponseEntity.ok("Event status updated.");
    }
}