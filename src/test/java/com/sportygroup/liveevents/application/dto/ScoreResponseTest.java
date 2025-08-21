package com.sportygroup.liveevents.application.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreResponseTest {

    @Test
    void shouldDeserializeFromJson() throws Exception {
        String json = "{\"eventId\":\"match-1\",\"currentScore\":\"Team A 1 - 0 Team B\"}";

        ObjectMapper mapper = new ObjectMapper();
        ScoreResponse response = mapper.readValue(json, ScoreResponse.class);

        assertEquals("match-1", response.eventId());
        assertEquals("Team A 1 - 0 Team B", response.currentScore());
    }
}
