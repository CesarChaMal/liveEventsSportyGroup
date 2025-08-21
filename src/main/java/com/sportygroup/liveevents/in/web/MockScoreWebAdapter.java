package com.sportygroup.liveevents.in.web;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// Mock web adapter for testing external score API calls
@WebAdapter
@RequestMapping("/api/events")
public class MockScoreWebAdapter {

    @GetMapping("/{eventId}/score")
    public ScoreResponse getScore(@PathVariable String eventId) {
        return new ScoreResponse(eventId, "Team A 1 - 0 Team B");
    }
}