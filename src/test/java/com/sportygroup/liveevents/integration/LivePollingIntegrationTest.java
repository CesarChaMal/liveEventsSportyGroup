package com.sportygroup.liveevents.integration;

import com.sportygroup.liveevents.LiveEventsApplication;
import com.sportygroup.liveevents.model.ScoreResponse;
import com.sportygroup.liveevents.service.EventTrackingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LiveEventsApplication.class)
@ActiveProfiles("test")
class LivePollingIntegrationTest {

    @Autowired
    private EventTrackingService trackingService;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void pollLiveEvent_shouldFetchAndPublish() {
        String eventId = "test-event";
        trackingService.updateEventStatus(eventId, "live");

        ScoreResponse mockResponse = new ScoreResponse(eventId, "Team X 2 - 1 Team Y");
        when(restTemplate.getForObject(anyString(), eq(ScoreResponse.class)))
                .thenReturn(mockResponse);

        // Wait long enough for scheduler to run at least once (e.g., 11s)
        try {
            Thread.sleep(11000);
        } catch (InterruptedException ignored) {
        }

        verify(kafkaTemplate, atLeastOnce()).send(any(), eq(eventId), eq("Team X 2 - 1 Team Y"));
    }
}
