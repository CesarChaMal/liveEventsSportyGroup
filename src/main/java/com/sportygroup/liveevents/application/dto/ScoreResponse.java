package com.sportygroup.liveevents.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable response representing current score of an event.
 */
public record ScoreResponse(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("currentScore") String currentScore
) {}
