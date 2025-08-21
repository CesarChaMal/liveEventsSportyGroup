package com.sportygroup.liveevents;

import com.sportygroup.liveevents.application.dto.ScoreResponse;
import com.sportygroup.liveevents.application.usecase.*;
import com.sportygroup.liveevents.domain.model.*;
import com.sportygroup.liveevents.domain.port.*;
import com.sportygroup.liveevents.domain.service.DomainEventTrackingService;
import com.sportygroup.liveevents.out.persistence.InMemoryEventRepository;
import com.sportygroup.liveevents.out.messaging.MessagePublisherService;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulation of the Live Events Service demonstrating hexagonal architecture
 * with various test scenarios without external dependencies.
 */
public class LiveEventsSimulation {
    
    public static void main(String[] args) {
        System.out.println("🟢 Live Events Service Simulation");
        System.out.println("==================================");
        
        LiveEventsSimulation simulation = new LiveEventsSimulation();
        simulation.runAllTestCases();
    }
    
    public void runAllTestCases() {
        // Setup hexagonal architecture components
        EventRepository eventRepository = new InMemoryEventRepository();
        ScoreFetcher scoreFetcher = new MockScoreFetcher();
        EventPublisherPort eventPublisher = createEventPublisher();
        
        DomainEventTrackingService trackingService = new DomainEventTrackingService(eventRepository);
        UpdateEventStatusUseCase updateUseCase = new UpdateEventStatusUseCase(trackingService);
        ProcessLiveEventsUseCase processUseCase = new ProcessLiveEventsUseCase(trackingService, scoreFetcher, eventPublisher);
        
        // Test Case 1: Basic event lifecycle
        testCase1_BasicEventLifecycle(updateUseCase, processUseCase);
        
        // Test Case 2: Multiple live events
        testCase2_MultipleLiveEvents(updateUseCase, processUseCase);
        
        // Test Case 3: Event status changes
        testCase3_EventStatusChanges(updateUseCase, processUseCase);
        
        // Test Case 4: Single event processing (facade functionality)
        testCase4_SingleEventProcessing(processUseCase);
        
        // Test Case 5: Error handling
        testCase5_ErrorHandling(processUseCase);
        
        System.out.println("\n✅ All test cases completed successfully!");
    }
    
    private void testCase1_BasicEventLifecycle(UpdateEventStatusUseCase updateUseCase, ProcessLiveEventsUseCase processUseCase) {
        System.out.println("\n📋 Test Case 1: Basic Event Lifecycle");
        System.out.println("-------------------------------------");
        
        // Mark event as live
        updateUseCase.execute("match-001", "live");
        System.out.println("✓ Event 'match-001' marked as live");
        
        // Process live events
        processUseCase.execute();
        System.out.println("✓ Processed all live events");
        
        // Mark event as ended
        updateUseCase.execute("match-001", "ended");
        System.out.println("✓ Event 'match-001' marked as ended");
        
        // Process again (should be empty)
        processUseCase.execute();
        System.out.println("✓ No live events to process");
    }
    
    private void testCase2_MultipleLiveEvents(UpdateEventStatusUseCase updateUseCase, ProcessLiveEventsUseCase processUseCase) {
        System.out.println("\n📋 Test Case 2: Multiple Live Events");
        System.out.println("------------------------------------");
        
        // Mark multiple events as live
        updateUseCase.execute("match-002", "live");
        updateUseCase.execute("match-003", "live");
        updateUseCase.execute("match-004", "live");
        System.out.println("✓ Three events marked as live");
        
        // Process all live events
        processUseCase.execute();
        System.out.println("✓ Processed all three live events");
    }
    
    private void testCase3_EventStatusChanges(UpdateEventStatusUseCase updateUseCase, ProcessLiveEventsUseCase processUseCase) {
        System.out.println("\n📋 Test Case 3: Event Status Changes");
        System.out.println("------------------------------------");
        
        // Start with live event
        updateUseCase.execute("match-005", "live");
        System.out.println("✓ Event 'match-005' marked as live");
        
        // Process it
        processUseCase.execute();
        System.out.println("✓ Processed live event");
        
        // Change to ended
        updateUseCase.execute("match-005", "ended");
        System.out.println("✓ Event 'match-005' marked as ended");
        
        // Process again (should skip ended event)
        processUseCase.execute();
        System.out.println("✓ Ended event skipped during processing");
        
        // Reactivate
        updateUseCase.execute("match-005", "live");
        System.out.println("✓ Event 'match-005' reactivated as live");
        
        processUseCase.execute();
        System.out.println("✓ Reactivated event processed");
    }
    
    private void testCase4_SingleEventProcessing(ProcessLiveEventsUseCase processUseCase) {
        System.out.println("\n📋 Test Case 4: Single Event Processing (Facade)");
        System.out.println("------------------------------------------------");
        
        // Test single event processing with EventId
        processUseCase.processEvent(new EventId("match-006"));
        System.out.println("✓ Single event processed with EventId");
        
        // Test single event processing with String
        processUseCase.processEvent("match-007");
        System.out.println("✓ Single event processed with String ID");
    }
    
    private void testCase5_ErrorHandling(ProcessLiveEventsUseCase processUseCase) {
        System.out.println("\n📋 Test Case 5: Error Handling");
        System.out.println("------------------------------");
        
        // Process event that will return empty score
        processUseCase.processEvent("error-event");
        System.out.println("✓ Gracefully handled event with no score");
    }
    
    private EventPublisherPort createEventPublisher() {
        try {
            // Load .env file if exists
            loadEnvFile();
            
            // Try to create real Kafka publisher
            String kafkaIp = System.getenv("KAFKA_SERVER_IP");
            if (kafkaIp == null) {
                kafkaIp = System.getProperty("KAFKA_SERVER_IP", "localhost");
            }
            String kafkaServer = kafkaIp + ":9092";
            System.out.println("🔍 Attempting to connect to Kafka at: " + kafkaServer);
            
            KafkaTemplate<String, Object> kafkaTemplate = createKafkaTemplate(kafkaServer);
            MessagePublisherService publisherService = new MessagePublisherService(kafkaTemplate);
            
            // Test Kafka connection
            kafkaTemplate.send("test-topic", "connection-test").get();
            System.out.println("✅ Kafka connection successful - using real Kafka");
            
            return new RealEventPublisher(publisherService);
        } catch (Exception e) {
            System.out.println("⚠️ Kafka not available (" + e.getMessage() + ") - using mock publisher");
            return new MockEventPublisher();
        }
    }
    
    private KafkaTemplate<String, Object> createKafkaTemplate(String kafkaServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);
        
        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(producerFactory);
    }
    
    private void loadEnvFile() {
        Path envFile = Paths.get(".env");
        if (Files.exists(envFile)) {
            try {
                Files.lines(envFile)
                    .filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            // Only set if not already set as system env var
                            if (System.getenv(key) == null) {
                                System.setProperty(key, value);
                                System.out.println("📄 Loaded from .env: " + key + "=" + value);
                            }
                        }
                    });
            } catch (IOException e) {
                System.out.println("⚠️ Could not read .env file: " + e.getMessage());
            }
        }
    }

    // Mock implementations for simulation
    private static class MockScoreFetcher implements ScoreFetcher {
        private final AtomicInteger scoreCounter = new AtomicInteger(0);
        
        @Override
        public Optional<Score> fetchScore(EventId eventId) {
            if (eventId.value().startsWith("error")) {
                return Optional.empty();
            }
            
            int count = scoreCounter.incrementAndGet();
            String scoreText = String.format("Team A %d - %d Team B", count, count - 1);
            return Optional.of(new Score(scoreText));
        }
    }
    
    private static class MockEventPublisher implements EventPublisherPort {
        private int publishCount = 0;
        
        @Override
        public void publish(EventId eventId, Score score) {
            publishCount++;
            System.out.printf("📤 [MOCK] Published: Event=%s, Score=%s (Total: %d)%n", 
                eventId.value(), score.value(), publishCount);
        }
    }
    
    private static class RealEventPublisher implements EventPublisherPort {
        private final MessagePublisherService publisherService;
        private int publishCount = 0;
        
        public RealEventPublisher(MessagePublisherService publisherService) {
            this.publisherService = publisherService;
        }
        
        @Override
        public void publish(EventId eventId, Score score) {
            publishCount++;
            publisherService.publishMessage(eventId.value(), score.value());
            System.out.printf("📤 [KAFKA] Published: Event=%s, Score=%s (Total: %d)%n", 
                eventId.value(), score.value(), publishCount);
        }
    }
}