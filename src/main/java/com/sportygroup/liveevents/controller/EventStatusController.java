package com.sportygroup.liveevents.controller;

import com.sportygroup.liveevents.model.EventStatusRequest;
import com.sportygroup.liveevents.service.EventTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventStatusController {

    private static final Logger logger = LoggerFactory.getLogger(EventStatusController.class);
    private final EventTrackingService trackingService;

    public EventStatusController(EventTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/status")
    @Tag(name = "Events", description = "Event status update operations")
    @Operation(summary = "Update the status of an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<String> updateEventStatus(@Valid @RequestBody EventStatusRequest request) {
        logger.info("Received status update for event {}: {}", request.eventId(), request.status());
        trackingService.updateEventStatus(request.eventId(), request.status());
        return ResponseEntity.ok("Event status updated.");
    }
}
