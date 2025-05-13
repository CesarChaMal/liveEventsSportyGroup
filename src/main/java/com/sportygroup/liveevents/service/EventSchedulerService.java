package com.sportygroup.liveevents.service;

import com.sportygroup.liveevents.model.ScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(EventSchedulerService.class);

    private final Map<String, Boolean> liveEvents = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;
    private final MessagePublisherService publisherService;

    public EventSchedulerService(RestTemplate restTemplate, MessagePublisherService publisherService) {
        this.restTemplate = restTemplate;
        this.publisherService = publisherService;
    }

    public void updateEventStatus(String eventId, String status) {
        if ("live".equalsIgnoreCase(status)) {
            liveEvents.put(eventId, true);
        } else {
            liveEvents.remove(eventId);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void pollLiveEvents() {
        for (String eventId : liveEvents.keySet()) {
            try {
                String url = "http://localhost:8081/api/events/" + eventId + "/score";
                ScoreResponse response = restTemplate.getForObject(url, ScoreResponse.class);

                if (response != null && response.getCurrentScore() != null) {
                    logger.info("📡 Fetched score for event {}: {}", eventId, response.getCurrentScore());
                    publisherService.publishMessage(eventId, response.getCurrentScore());
                } else {
                    logger.warn("⚠ No score received for event {}", eventId);
                }
            } catch (Exception e) {
                logger.error("❌ Failed to fetch or publish score for event {}: {}", eventId, e.getMessage());
            }
        }
    }

    // 🔍 Added for test visibility
    public Map<String, Boolean> getLiveEvents() {
        return liveEvents;
    }
}
