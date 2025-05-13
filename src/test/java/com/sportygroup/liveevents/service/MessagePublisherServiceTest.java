package com.sportygroup.liveevents.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public class MessagePublisherServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private MessagePublisherService publisherService;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublishMessage_shouldHandleSuccess() {
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq("live-events"), eq("event-key"), any()))
                .thenReturn(future);

        assertDoesNotThrow(() -> publisherService.publishMessage("event-key", "payload"));
        verify(kafkaTemplate).send("live-events", "event-key", "payload");
    }

    @Test
    void testPublishMessage_shouldHandleFailure() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka failed"));

        when(kafkaTemplate.send(eq("live-events"), eq("event-key"), any()))
                .thenReturn(future);

        assertDoesNotThrow(() -> publisherService.publishMessage("event-key", "payload"));
    }
}
