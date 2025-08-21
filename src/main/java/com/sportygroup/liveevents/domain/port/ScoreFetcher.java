package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.domain.model.*;
import java.util.Optional;

public interface ScoreFetcher {
    Optional<Score> fetchScore(EventId eventId);
}