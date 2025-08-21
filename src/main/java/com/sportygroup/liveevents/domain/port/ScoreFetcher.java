package com.sportygroup.liveevents.domain.port;

import com.sportygroup.liveevents.domain.model.*;
import java.util.Optional;

// This interface replaces the old EventFetcher interface with proper hexagonal architecture
// using domain models instead of application DTOs
public interface ScoreFetcher {
    Optional<Score> fetchScore(EventId eventId);
}