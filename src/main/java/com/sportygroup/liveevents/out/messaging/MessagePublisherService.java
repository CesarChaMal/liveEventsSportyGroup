package com.sportygroup.liveevents.out.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MessagePublisherService {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisherService.class);
    private static final String TOPIC = "live-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public MessagePublisherService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void publishMessage(String eventId, String payload) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, eventId, payload);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("❌ Failed to publish message to Kafka: {}", ex.getMessage());
            } else {
                logger.info("✅ Published message to Kafka: {} -> {}", eventId, payload);
            }
        });
    }
}
