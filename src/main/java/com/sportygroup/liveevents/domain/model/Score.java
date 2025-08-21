package com.sportygroup.liveevents.domain.model;

public record Score(String value) {
    public Score {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Score cannot be null or blank");
        }
    }
}