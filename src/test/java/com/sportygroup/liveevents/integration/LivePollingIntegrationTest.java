package com.sportygroup.liveevents.integration;

import com.sportygroup.liveevents.LiveEventsApplication;
import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.application.usecase.UpdateEventStatusUseCase;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LiveEventsApplication.class)
@ActiveProfiles("test")
class LivePollingIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(LivePollingIntegrationTest.class);

    @Autowired
    private UpdateEventStatusUseCase updateEventStatusUseCase;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void pollLiveEvent_shouldFetchAndPublish() {
        String eventId = "test-event";
        logger.debug("Marking event '{}' as live", eventId);
        updateEventStatusUseCase.execute(eventId, "live");

        ScoreResponse mockResponse = new ScoreResponse(eventId, "Team X 2 - 1 Team Y");
        when(restTemplate.getForObject(anyString(), eq(ScoreResponse.class)))
                .thenReturn(mockResponse);
        logger.debug("Mocked REST response for event '{}'", eventId);

        logger.debug("Waiting up to 15 seconds for Kafka send verification...");
        await()
                .atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(kafkaTemplate, atLeastOnce()).send(any(), eq(eventId), eq("Team X 2 - 1 Team Y"));
                    logger.debug("Verified Kafka send for event '{}'", eventId);
                });
    }
}
