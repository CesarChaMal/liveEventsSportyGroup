package com.sportygroup.liveevents.service.fetcher;

import com.sportygroup.liveevents.model.ScoreResponse;

import java.util.Optional;

public interface EventFetcher {
    Optional<ScoreResponse> fetchScore(String eventId);
}
