package com.sportygroup.liveevents.out.external;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.domain.model.EventId;
import com.sportygroup.liveevents.domain.model.Score;
import com.sportygroup.liveevents.domain.port.ScoreFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

// This class replaces the old RestEventFetcher implementation with proper hexagonal architecture
@Component
public class RestScoreFetcher implements ScoreFetcher {
    private static final Logger logger = LoggerFactory.getLogger(RestScoreFetcher.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestScoreFetcher(RestTemplate restTemplate,
                            @Value("${events.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<Score> fetchScore(EventId eventId) {
        try {
            String url = baseUrl + "/" + eventId.value() + "/score";
            ScoreResponse response = restTemplate.getForObject(url, ScoreResponse.class);
            return Optional.ofNullable(response)
                    .map(ScoreResponse::currentScore)
                    .map(Score::new);
        } catch (Exception e) {
            logger.error("❌ Error fetching score for {}: {}", eventId.value(), e.getMessage());
            return Optional.empty();
        }
    }
}