package com.sportygroup.liveevents.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class ScoreResponse {

    private final String eventId;
    private final String currentScore;

    @JsonCreator
    public ScoreResponse(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("currentScore") String currentScore
    ) {
        this.eventId = eventId;
        this.currentScore = currentScore;
    }

    public String getEventId() {
        return eventId;
    }

    public String getCurrentScore() {
        return currentScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoreResponse)) return false;
        ScoreResponse that = (ScoreResponse) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(currentScore, that.currentScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, currentScore);
    }

    @Override
    public String toString() {
        return "ScoreResponse{" +
                "eventId='" + eventId + '\'' +
                ", currentScore='" + currentScore + '\'' +
                '}';
    }
}
