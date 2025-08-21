package com.sportygroup.liveevents.out.external;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.domain.port.EventFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class RestEventFetcher implements EventFetcher {

    private static final Logger logger = LoggerFactory.getLogger(RestEventFetcher.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public RestEventFetcher(RestTemplate restTemplate,
                            @Value("${events.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<ScoreResponse> fetchScore(String eventId) {
        try {
            String url = baseUrl + "/" + eventId + "/score";
            ScoreResponse response = restTemplate.getForObject(url, ScoreResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            logger.error("❌ Error fetching score for {}: {}", eventId, e.getMessage());
            return Optional.empty();
        }
    }
}
