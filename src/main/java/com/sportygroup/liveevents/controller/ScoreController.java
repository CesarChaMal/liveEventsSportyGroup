package com.sportygroup.liveevents.controller;

import com.sportygroup.liveevents.model.ScoreResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
public class ScoreController {

    @GetMapping("/{eventId}/score")
    public ScoreResponse getScore(@PathVariable String eventId) {
        return new ScoreResponse(eventId, "Team A 1 - 0 Team B");
    }
}
