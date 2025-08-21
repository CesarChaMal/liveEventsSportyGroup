package com.sportygroup.liveevents.out.messaging;

import com.sportygroup.liveevents.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class KafkaEventPublisherAdapterTest {

    @Mock
    private MessagePublisherService messagePublisher;

    private KafkaEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new KafkaEventPublisherAdapter(messagePublisher);
    }

    @Test
    void shouldPublishEvent() {
        EventId eventId = new EventId("match-123");
        Score score = new Score("Team A 2-1 Team B");

        adapter.publish(eventId, score);

        verify(messagePublisher).publishMessage("match-123", "Team A 2-1 Team B");
    }
}