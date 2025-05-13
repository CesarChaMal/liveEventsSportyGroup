package com.sportygroup.liveevents.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable response representing current score of an event.
 */
public record ScoreResponse(
        @JsonProperty("eventId") String eventId,
        @JsonProperty("currentScore") String currentScore
) {
    @JsonCreator
    public ScoreResponse {}
}
