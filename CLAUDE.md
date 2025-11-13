# CLAUDE.md - AI Assistant Guide for Live Events Service

> **Last Updated**: 2025-11-13
> **Version**: v0.5.0
> **Purpose**: This document provides comprehensive guidance for AI assistants working with the Live Events Service codebase.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Directory Structure](#directory-structure)
4. [Development Conventions](#development-conventions)
5. [Key Patterns and Practices](#key-patterns-and-practices)
6. [Testing Guidelines](#testing-guidelines)
7. [Build and Deployment](#build-and-deployment)
8. [Configuration](#configuration)
9. [Common Tasks](#common-tasks)
10. [Integration Points](#integration-points)
11. [Important Locations](#important-locations)

---

## 🎯 Project Overview

### What This Service Does

The **Live Events Service** is a production-ready Spring Boot application that:
- Accepts live event status updates via REST API (`POST /events/status`)
- Periodically polls external systems for updated scores (every 10 seconds)
- Publishes live scores to Kafka topic `live-events`
- Uses configurable retry logic for resilient message publishing
- Provides health checks and metrics via Spring Boot Actuator

### Technology Stack

- **Java**: 17 (with Records for immutability)
- **Framework**: Spring Boot 3.1.0
- **Messaging**: Spring Kafka
- **Build Tool**: Maven 3.x
- **Testing**: JUnit 5, Mockito, Awaitility
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions

### Key Characteristics

- ✅ **Hexagonal Architecture** (Ports & Adapters pattern)
- ✅ **SOLID Principles** throughout
- ✅ **Immutable Domain Models** using Java 17 records
- ✅ **Comprehensive Testing** (19 test files, mirrors source structure)
- ✅ **Framework-Independent Domain** (zero Spring dependencies in domain layer)
- ✅ **Production Ready** with actuator endpoints and CI/CD

---

## 🏛️ Architecture

### Hexagonal Architecture Overview

This project strictly follows **Hexagonal (Ports & Adapters) Architecture** with clear layer boundaries:

```
┌─────────────────────────────────────────────────────────────┐
│                    INBOUND ADAPTERS                          │
│  (REST Controllers, Schedulers - trigger use cases)          │
│  Location: src/main/java/.../in/                             │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   APPLICATION LAYER                          │
│  (Use Cases - orchestrate domain operations)                 │
│  Location: src/main/java/.../application/                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  (Business Logic - pure, framework-independent)              │
│  • Models (Event, EventId, Score, EventStatus)               │
│  • Services (DomainEventTrackingService)                     │
│  • Ports (EventRepository, ScoreFetcher, EventPublisherPort) │
│  Location: src/main/java/.../domain/                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   OUTBOUND ADAPTERS                          │
│  (Implement domain ports with specific technologies)         │
│  • Persistence: InMemoryEventRepository                      │
│  • External: RestScoreFetcher                                │
│  • Messaging: KafkaEventPublisherAdapter                     │
│  Location: src/main/java/.../out/                            │
└─────────────────────────────────────────────────────────────┘
```

### Critical Architectural Rules

🚫 **NEVER violate these dependency rules:**

1. **Domain Layer** (`domain/`)
   - ✅ Can depend on: Nothing (only standard Java libraries)
   - ❌ Cannot depend on: Spring, JPA, Kafka, any framework
   - ❌ NO annotations: `@Service`, `@Component`, `@Repository`, `@RestController`
   - ✅ Uses: `@DomainService` (custom annotation, no Spring dependency)

2. **Application Layer** (`application/`)
   - ✅ Can depend on: Domain layer only
   - ❌ Cannot depend on: Adapters, infrastructure, specific implementations
   - ✅ Uses: `@UseCase` annotation (extends `@Component`)

3. **Adapters** (`in/`, `out/`)
   - ✅ Can depend on: Domain, Application layers
   - ❌ Cannot depend on: Other adapters
   - ✅ Uses: `@WebAdapter`, `@ExternalAdapter`, `@PersistenceAdapter`

4. **Infrastructure** (`infrastructure/`)
   - ✅ Can depend on: Everything (wires components together)
   - Purpose: Configuration only, no business logic

### Data Flow Examples

**Incoming HTTP Request:**
```
HTTP POST → EventStatusController (@WebAdapter)
          → UpdateEventStatusUseCase (@UseCase)
          → DomainEventTrackingService (@DomainService)
          → EventRepository (port interface)
          → InMemoryEventRepository (@PersistenceAdapter)
```

**Scheduled Polling:**
```
@Scheduled → PollingSchedulerAdapter (inbound)
           → ProcessLiveEventsUseCase (@UseCase)
           → ScoreFetcher (port interface)
           → RestScoreFetcher (@ExternalAdapter)
           → EventPublisherPort (port interface)
           → KafkaEventPublisherAdapter (outbound)
```

---

## 📁 Directory Structure

### Source Code Layout

```
src/main/java/com/sportygroup/liveevents/
├── LiveEventsApplication.java          # Spring Boot entry point
├── LiveEventsSimulation.java           # Standalone simulation (no Spring)
│
├── common/                              # Custom architectural annotations
│   ├── DomainService.java              # Marks domain services (no Spring)
│   ├── UseCase.java                    # Marks use cases (@Component)
│   ├── WebAdapter.java                 # Marks REST controllers (@RestController)
│   ├── ExternalAdapter.java            # Marks external clients (@Component)
│   └── PersistenceAdapter.java         # Marks repositories (@Repository)
│
├── domain/                              # ⚠️ PURE BUSINESS LOGIC - NO FRAMEWORKS
│   ├── model/                          # Value objects & entities
│   │   ├── Event.java                  # Aggregate root
│   │   ├── EventId.java                # Record - immutable ID
│   │   ├── EventStatus.java            # Enum (LIVE, ENDED)
│   │   └── Score.java                  # Record - immutable score data
│   ├── service/                        # Domain services
│   │   └── DomainEventTrackingService.java  # Business rules
│   └── port/                           # Interfaces for external systems
│       ├── EventRepository.java        # Persistence contract
│       ├── ScoreFetcher.java          # Score retrieval contract
│       └── EventPublisherPort.java    # Event publishing contract
│
├── application/                         # Use cases & orchestration
│   ├── usecase/
│   │   ├── UpdateEventStatusUseCase.java
│   │   └── ProcessLiveEventsUseCase.java
│   └── dto/                            # Data Transfer Objects
│       ├── EventStatusRequest.java     # With @Validated
│       └── ScoreResponse.java          # Immutable record
│
├── in/                                  # ⬇️ INBOUND ADAPTERS (entry points)
│   ├── web/                            # REST API
│   │   ├── EventStatusController.java  # @WebAdapter
│   │   └── MockScoreWebAdapter.java   # Mock score endpoint
│   └── scheduler/                      # Scheduled tasks
│       └── PollingSchedulerAdapter.java # @Scheduled
│
├── out/                                 # ⬆️ OUTBOUND ADAPTERS (implementations)
│   ├── persistence/                    # Data storage
│   │   └── InMemoryEventRepository.java # @PersistenceAdapter
│   ├── external/                       # External APIs
│   │   └── RestScoreFetcher.java      # @ExternalAdapter
│   └── messaging/                      # Message publishing
│       ├── KafkaEventPublisherAdapter.java
│       └── MessagePublisherService.java # Kafka producer
│
└── infrastructure/                      # ⚙️ CONFIGURATION
    ├── config/
    │   ├── HexagonalConfig.java       # Wires components
    │   ├── KafkaConfig.java           # Kafka setup
    │   └── SwaggerConfig.java         # API docs
    └── adapter/                        # Spring service bridges
        ├── EventTrackingService.java
        └── PollingSchedulerService.java
```

### Test Structure

```
src/test/java/com/sportygroup/liveevents/
├── domain/                             # Domain logic tests
│   └── service/
│       └── DomainEventTrackingServiceTest.java
├── application/
│   └── usecase/
│       ├── UpdateEventStatusUseCaseTest.java
│       └── ProcessLiveEventsUseCaseTest.java
├── in/
│   ├── web/
│   │   └── EventStatusControllerTest.java
│   └── scheduler/
│       └── PollingSchedulerAdapterTest.java
├── out/
│   ├── persistence/
│   │   └── InMemoryEventRepositoryTest.java
│   ├── external/
│   │   └── RestScoreFetcherTest.java
│   └── messaging/
│       ├── KafkaEventPublisherAdapterTest.java
│       └── MessagePublisherServiceTest.java
└── infrastructure/
    └── adapter/
        ├── EventTrackingServiceTest.java
        └── PollingSchedulerServiceTest.java
```

**Testing Convention**: Test package structure **mirrors** source package structure exactly.

---

## 🎨 Development Conventions

### Custom Annotations

This project uses **semantic annotations** to indicate architectural roles:

| Annotation | Purpose | Extends | Location |
|------------|---------|---------|----------|
| `@DomainService` | Pure business logic | None | `domain/service/` |
| `@UseCase` | Application use case | `@Component` | `application/usecase/` |
| `@WebAdapter` | REST controller | `@RestController` | `in/web/` |
| `@ExternalAdapter` | External client | `@Component` | `out/external/` |
| `@PersistenceAdapter` | Repository impl | `@Repository` | `out/persistence/` |

**Usage Example:**
```java
// Domain service - NO Spring dependency
@DomainService
public class DomainEventTrackingService {
    // Pure business logic
}

// Use case - Spring-managed
@UseCase
public class UpdateEventStatusUseCase {
    // Orchestrates domain operations
}
```

### Immutability Patterns

**Use Java 17 Records for:**
- Value objects (EventId, Score)
- DTOs (EventStatusRequest, ScoreResponse)
- Anything that should be immutable

```java
// Good - immutable value object
public record EventId(String value) {
    public EventId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be null or blank");
        }
    }
}

// Bad - mutable POJO
public class EventId {
    private String value; // ❌ Don't do this
    public void setValue(String value) { ... }
}
```

### Validation

- Use **Jakarta Bean Validation** (`@NotNull`, `@NotBlank`, `@Validated`)
- Validate at **boundaries** (DTOs, controller inputs)
- Use **defensive programming** in domain constructors

```java
public record EventStatusRequest(
    @NotNull @NotBlank String eventId,
    @NotNull @NotBlank String status
) {}
```

### Dependency Injection

- **Always use constructor injection** (no `@Autowired` on fields)
- Make dependencies `final`
- Use interfaces (ports) for dependencies

```java
// Good
@UseCase
public class UpdateEventStatusUseCase {
    private final EventRepository eventRepository;

    public UpdateEventStatusUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}

// Bad
@UseCase
public class UpdateEventStatusUseCase {
    @Autowired // ❌ Avoid field injection
    private EventRepository eventRepository;
}
```

### Naming Conventions

| Type | Convention | Example |
|------|-----------|---------|
| Use Cases | `{Verb}{Noun}UseCase` | `UpdateEventStatusUseCase` |
| Domain Services | `Domain{Noun}Service` | `DomainEventTrackingService` |
| Ports (interfaces) | `{Noun}{Verb}` or `{Noun}Port` | `ScoreFetcher`, `EventPublisherPort` |
| Adapters | `{Tech}{Noun}Adapter` | `KafkaEventPublisherAdapter` |
| DTOs | `{Noun}Request/Response` | `EventStatusRequest` |
| Tests | `{ClassName}Test` | `UpdateEventStatusUseCaseTest` |

---

## 🔑 Key Patterns and Practices

### 1. Ports Pattern

**Ports are interfaces** in the domain layer that define what the domain needs from external systems.

```java
// domain/port/EventRepository.java
public interface EventRepository {
    void save(Event event);
    Optional<Event> findById(EventId id);
    List<Event> findByStatus(EventStatus status);
}
```

**Adapters implement ports** in the outbound layer:

```java
// out/persistence/InMemoryEventRepository.java
@PersistenceAdapter
public class InMemoryEventRepository implements EventRepository {
    // Implementation using ConcurrentHashMap
}
```

### 2. Use Case Pattern

Use cases orchestrate domain operations and are the **entry points** to business logic.

```java
@UseCase
public class ProcessLiveEventsUseCase {
    private final EventRepository repository;
    private final ScoreFetcher scoreFetcher;
    private final EventPublisherPort publisher;

    public void execute() {
        // 1. Get live events from repository
        // 2. Fetch scores via ScoreFetcher
        // 3. Publish via EventPublisherPort
    }
}
```

### 3. Immutable Domain Models

Use records for value objects:

```java
public record Score(String eventId, int homeScore, int awayScore) {
    // Compact constructor for validation
    public Score {
        if (eventId == null) throw new IllegalArgumentException();
        if (homeScore < 0 || awayScore < 0) throw new IllegalArgumentException();
    }
}
```

### 4. Retry Logic

Use Spring Retry for resilience:

```java
@Retryable(
    value = Exception.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000)
)
public void publishScore(ScoreResponse score) {
    // Kafka publishing with retry
}
```

### 5. Scheduled Tasks

Use `@Scheduled` in adapters, not domain:

```java
@Component
public class PollingSchedulerAdapter {
    private final ProcessLiveEventsUseCase useCase;

    @Scheduled(fixedDelayString = "${polling.interval}")
    public void pollLiveEvents() {
        useCase.execute();
    }
}
```

---

## 🧪 Testing Guidelines

### Test Structure

1. **Unit Tests**: Test classes in isolation with mocks
2. **Integration Tests**: Test with Spring context (`@SpringBootTest`)
3. **Test Location**: Mirror source package structure

### Testing Each Layer

#### Domain Layer Tests
- **No Spring context** required
- Test pure business logic
- Use plain JUnit 5

```java
class DomainEventTrackingServiceTest {
    private DomainEventTrackingService service;

    @BeforeEach
    void setUp() {
        service = new DomainEventTrackingService();
    }

    @Test
    void shouldMarkEventAsLive() {
        // Pure unit test, no mocks needed
    }
}
```

#### Use Case Tests
- Mock dependencies (ports)
- Verify orchestration logic
- Use Mockito

```java
class UpdateEventStatusUseCaseTest {
    @Mock private EventRepository repository;
    @InjectMocks private UpdateEventStatusUseCase useCase;

    @Test
    void shouldUpdateEventStatus() {
        // Arrange: Setup mocks
        when(repository.findById(any())).thenReturn(Optional.of(event));

        // Act: Execute use case
        useCase.execute(request);

        // Assert: Verify interactions
        verify(repository).save(any());
    }
}
```

#### Adapter Tests
- Test Spring integration for outbound adapters
- Mock external dependencies (RestTemplate, KafkaTemplate)
- Use `@SpringBootTest` for integration tests

```java
@SpringBootTest
class KafkaEventPublisherAdapterTest {
    @MockBean private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired private KafkaEventPublisherAdapter adapter;

    @Test
    void shouldPublishScoreToKafka() {
        // Test with Spring context
    }
}
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UpdateEventStatusUseCaseTest

# Run with coverage
mvn clean verify

# Skip tests during build
mvn clean install -DskipTests
```

---

## 🔨 Build and Deployment

### Maven Profiles

The project supports **3 profiles**:

| Profile | Activate | Purpose |
|---------|----------|---------|
| `dev` | `mvn -Pdev` | Development with mock Kafka |
| `test` | `mvn -Ptest` | Testing environment |
| `prod` | Default | Production configuration |

### Build Commands

```bash
# Clean build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Build and verify (runs tests + integration tests)
mvn clean verify

# Run application
mvn spring-boot:run

# Run with custom Kafka server
KAFKA_SERVER_IP=172.27.11.4 mvn spring-boot:run

# Package JAR
mvn package
java -jar target/live-events-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
# Build image
docker build -t live-events-app .

# Run with Docker Compose (includes Kafka, Zookeeper, MockServer)
docker-compose up

# Run with custom Kafka
KAFKA_SERVER_IP=172.27.11.4 docker-compose up

# Kafka only (for development)
docker-compose -f src/main/docker/kafka.yml up
```

### CI/CD

**GitHub Actions** (`.github/workflows/ci.yml`):
- Triggers on: Push to `main`, Pull Requests
- Runs: `mvn clean verify`
- Java: 17 (Temurin distribution)

---

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `KAFKA_SERVER_IP` | `localhost` | Kafka broker IP address |
| `POLLING_INTERVAL` | `10000` | Polling interval in milliseconds |
| `EVENTS_BASE_URL` | `http://localhost:8080/api/events` | External events API URL |

### Application Properties

**Location**: `src/main/resources/application.properties`

```properties
# Active profile
spring.profiles.active=dev

# External API
events.base-url=${EVENTS_BASE_URL:http://localhost:8080/api/events}

# Polling configuration
polling.interval=${POLLING_INTERVAL:10000}

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_SERVER_IP:localhost}:9092

# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### Profile-Specific Configuration

- `application-dev.properties` - Development settings
- `application-test.properties` - Test settings
- Default `application.properties` - Production settings

---

## 🔧 Common Tasks

### Adding a New Use Case

1. **Create use case** in `application/usecase/`:
```java
@UseCase
public class MyNewUseCase {
    private final SomePort port;

    public MyNewUseCase(SomePort port) {
        this.port = port;
    }

    public void execute(InputDTO input) {
        // Orchestrate domain operations
    }
}
```

2. **Create test** in `src/test/.../application/usecase/`:
```java
class MyNewUseCaseTest {
    @Mock private SomePort port;
    @InjectMocks private MyNewUseCase useCase;

    @Test
    void shouldExecuteSuccessfully() {
        // Test logic
    }
}
```

3. **Wire up** in controller or scheduler (inbound adapter)

### Adding a New Domain Model

1. **Create record** in `domain/model/`:
```java
public record MyModel(String id, String name) {
    public MyModel {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID required");
        }
    }
}
```

2. **NO Spring annotations** in domain layer

3. **Test** with plain JUnit (no Spring context)

### Adding a New Port

1. **Define interface** in `domain/port/`:
```java
public interface MyNewPort {
    void doSomething(MyModel model);
}
```

2. **Create adapter** in `out/{category}/`:
```java
@ExternalAdapter  // or @PersistenceAdapter
public class MyNewAdapter implements MyNewPort {
    @Override
    public void doSomething(MyModel model) {
        // Implementation
    }
}
```

3. **Inject into use case** via constructor

### Adding a New REST Endpoint

1. **Create controller** in `in/web/`:
```java
@WebAdapter
@RequestMapping("/api/my-resource")
public class MyResourceController {
    private final MyUseCase useCase;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MyRequest request) {
        useCase.execute(request);
        return ResponseEntity.ok("Success");
    }
}
```

2. **Create DTO** in `application/dto/`:
```java
public record MyRequest(
    @NotNull @NotBlank String field
) {}
```

3. **Test** with `@WebMvcTest` or `@SpringBootTest`

### Changing Configuration

1. **Update** `application.properties` or environment-specific file
2. **Use property injection**:
```java
@Value("${my.property:defaultValue}")
private String myProperty;
```

3. **Document** environment variables in README.md

---

## 🔗 Integration Points

### Kafka

- **Topic**: `live-events`
- **Bootstrap Servers**: `${KAFKA_SERVER_IP}:9092`
- **Producer Config**: `infrastructure/config/KafkaConfig.java`
- **Publishing**: `out/messaging/KafkaEventPublisherAdapter.java`
- **Retry**: 3 attempts, 2-second backoff

### External Score API

- **Base URL**: `${EVENTS_BASE_URL}`
- **Endpoint**: `GET /api/events/{eventId}/score`
- **Implementation**: `out/external/RestScoreFetcher.java`
- **Polling**: Every 10 seconds (configurable)

### REST API

- **Base Path**: `/events`
- **Endpoints**:
  - `POST /events/status` - Update event status
- **Documentation**: `http://localhost:8080/swagger-ui/index.html`

### Actuator

- **Health**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

---

## 📍 Important Locations

### Key Files to Check First

| Purpose | Location |
|---------|----------|
| Entry point | `src/main/java/.../LiveEventsApplication.java` |
| Main config | `src/main/resources/application.properties` |
| Dependencies | `pom.xml` |
| Architecture doc | `HEXAGONAL_ARCHITECTURE.md` |
| User README | `README.md` |
| CI/CD | `.github/workflows/ci.yml` |
| Docker | `Dockerfile`, `docker-compose.yml` |

### Domain Core (Start Here)

1. `domain/model/Event.java` - Aggregate root
2. `domain/port/` - All port interfaces
3. `domain/service/DomainEventTrackingService.java` - Business rules

### Primary Use Cases

1. `application/usecase/UpdateEventStatusUseCase.java` - Handle status updates
2. `application/usecase/ProcessLiveEventsUseCase.java` - Poll and publish

### Entry Points

1. `in/web/EventStatusController.java` - REST API
2. `in/scheduler/PollingSchedulerAdapter.java` - Scheduled polling

---

## 💡 AI Assistant Best Practices

### When Making Changes

1. **Identify the layer** first (domain/application/adapter/infrastructure)
2. **Check dependencies** - ensure you're not violating architecture rules
3. **Follow existing patterns** - look at similar classes first
4. **Create tests** - mirror the source structure
5. **Update documentation** - if changing public APIs or configuration

### Code Review Checklist

- [ ] Domain layer has no framework dependencies
- [ ] Use cases only depend on domain
- [ ] Adapters don't contain business logic
- [ ] Custom annotations used correctly
- [ ] Records used for immutable data
- [ ] Constructor injection for dependencies
- [ ] Tests created in mirrored structure
- [ ] Validation added at boundaries
- [ ] Configuration externalized properly

### Common Mistakes to Avoid

❌ **Don't**:
- Add Spring annotations to domain layer
- Put business logic in controllers
- Use field injection (`@Autowired` on fields)
- Create mutable DTOs or value objects
- Skip tests
- Hard-code configuration values
- Make adapters depend on each other

✅ **Do**:
- Keep domain pure
- Use custom architectural annotations
- Inject via constructors
- Use Java 17 records
- Write tests that mirror source structure
- Externalize configuration
- Follow dependency rules

### Understanding Before Coding

Before making changes:
1. Read `HEXAGONAL_ARCHITECTURE.md` if unfamiliar with the pattern
2. Identify which layer your change belongs to
3. Look at existing similar code for patterns
4. Check tests to understand expected behavior
5. Review relevant port interfaces

---

## 📚 Additional Resources

- **Hexagonal Architecture**: See `HEXAGONAL_ARCHITECTURE.md` for detailed explanation
- **Project README**: See `README.md` for user-facing documentation
- **API Docs**: `http://localhost:8080/swagger-ui/index.html` (when running)
- **CI Build**: `.github/workflows/ci.yml` for pipeline details

---

## 🔄 Keeping This Document Updated

When making significant changes:
- Update this document if architecture or conventions change
- Update version number at the top
- Update last updated date
- Document new patterns or practices
- Add new integration points

---

**End of CLAUDE.md** - Happy coding! 🚀
