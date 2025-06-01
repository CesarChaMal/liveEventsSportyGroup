package com.sportygroup.liveevents.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Immutable record-based DTO for event status updates.
 */
public record EventStatusRequest(
        @NotBlank String eventId,
        @Pattern(regexp = "live|ended") String status
) {}