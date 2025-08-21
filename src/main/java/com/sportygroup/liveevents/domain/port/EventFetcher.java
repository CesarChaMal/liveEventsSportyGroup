package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import java.util.Optional;

public interface EventFetcher {
    Optional<ScoreResponse> fetchScore(String eventId);
}
