# 🟢 Live Events Service

![CI](https://github.com/CesarChaMal/liveEventsSportyGroup/actions/workflows/ci.yml/badge.svg)


## 📌 Overview

The **Live Events Service** is a modular, testable Spring Boot backend application that:

- Accepts live event status updates via a REST API
- Periodically polls an external system for updated scores
- Publishes live scores to a Kafka topic using configurable retry logic
- Leverages the Facade, Adapter, and Ports and Adapters (Hexagonal) design patterns
- Applies SOLID principles, immutability, validation, and clean separation of concerns
- Is instrumented with Spring Boot Actuator for operational metrics and health checks
- Includes full unit and integration test coverage for critical paths

This is a backend-only service — no frontend layer is included.

---

## 🚀 Quick Start
### Prerequisites

- Java 17+
- Maven 3.x
- (Optional) Kafka broker - can run locally via Docker or remote server
- (Optional) Swagger/OpenAPI UI for API testing

---

### 🔧 Build & Run

```bash
# Build the application
mvn clean install

# Run with default Kafka (localhost:9092)
mvn spring-boot:run

# Run with custom Kafka server
KAFKA_SERVER_IP=172.27.11.4 mvn spring-boot:run
```

Or run the packaged JAR:

```bash
# Default Kafka server
java -jar target/live-events-0.0.1-SNAPSHOT.jar

# Custom Kafka server
KAFKA_SERVER_IP=172.27.11.4 java -jar target/live-events-0.0.1-SNAPSHOT.jar
```

---

### 🔁 REST Endpoint

#### `POST /events/status`

Marks an event as `live` or not, so it can be polled periodically.

**Request Example:**

```json
{
  "eventId": "123",
  "status": "live"
}
```

**Response:**

```json
"Event status updated."
```

---

### 🔁 Scheduled REST Polling

Every 10 seconds, the service calls:

```
GET http://localhost:8081/api/events/{eventId}/score
```

If the response is successful, the score is published to Kafka.

---

### 📨 Kafka Integration

Messages are published to the topic:

```
live-events
```

Message format: the score returned from the external `/score` endpoint.

---

## 🛠 Configuration

### Environment Variables

- `KAFKA_SERVER_IP` - Kafka server IP address (default: localhost)

### Application Properties

- `polling.interval` - Polling interval in milliseconds (default: 10000)
- `events.base-url` - External events API base URL

### Examples

```bash
# Set Kafka server IP
export KAFKA_SERVER_IP=172.27.11.4

# Docker
docker run -e KAFKA_SERVER_IP=172.27.11.4 live-events-app
```

---

## 🧪 Testing

Unit tests are written using:

- JUnit 5
- Mockito

### Included Test Classes
- PollingSchedulerServiceTest – Mocks RestTemplate and MessagePublisherService to verify score polling and Kafka publishing.
- MessagePublisherServiceTest – Verifies success and failure handling of Kafka message publication using CompletableFuture.
- EventStatusControllerTest – Tests REST endpoint /events/status by mocking the PollingSchedulerService.
```bash
# Run all tests
mvn test
```

---

## 📚 API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🎮 Live Events Simulation

Run the standalone simulation to see hexagonal architecture in action:

```bash
# Run simulation (tries real Kafka, falls back to mock)
java -cp target/classes com.sportygroup.liveevents.LiveEventsSimulation

# With custom Kafka server
KAFKA_SERVER_IP=172.27.11.4 java -cp target/classes com.sportygroup.liveevents.LiveEventsSimulation
```

**Features:**
- ✅ **Smart Kafka Detection** - Uses real Kafka if available, mock if not
- ✅ **Complete Test Scenarios** - 5 comprehensive test cases
- ✅ **No External Dependencies** - Runs standalone without Spring Boot
- ✅ **Environment Configurable** - Respects `KAFKA_SERVER_IP` variable

## ✅ Example cURL

```bash
curl -X POST http://localhost:8080/events/status \
-H "Content-Type: application/json" \
-d '{"eventId": "match-123", "status": "live"}'
```

## 🏗️ Hexagonal Architecture

This application implements **Hexagonal Architecture (Ports & Adapters)** with clean separation of concerns:

### 🎯 Domain Layer (Business Logic)
```
src/main/java/com/sportygroup/liveevents/domain/
├── model/          # EventId, EventStatus, Event, Score
├── service/        # DomainEventTrackingService
└── port/           # EventRepository, ScoreFetcher, EventPublisherPort
```
- **Pure business logic** with no external dependencies
- **Domain models** as immutable records (EventId, Score)
- **Domain services** for business rules
- **Ports** define contracts for external systems

### 🔄 Application Layer (Use Cases)
```
src/main/java/com/sportygroup/liveevents/application/
├── usecase/        # UpdateEventStatusUseCase, ProcessLiveEventsUseCase
└── dto/            # EventStatusRequest, ScoreResponse
```
- **Use cases** orchestrate domain operations
- **DTOs** for data transfer between layers

### ⬇️ Inbound Adapters (Entry Points)
```
src/main/java/com/sportygroup/liveevents/in/
├── web/            # REST controllers
└── scheduler/      # Scheduled tasks
```
- **Web controllers** handle HTTP requests
- **Schedulers** trigger periodic operations

### ⬆️ Outbound Adapters (External Systems)
```
src/main/java/com/sportygroup/liveevents/out/
├── persistence/    # Database implementations
├── external/       # External API clients
└── messaging/      # Kafka publishers
```
- **Repository implementations** for data persistence
- **External clients** for third-party APIs
- **Message publishers** for event streaming

### ⚙️ Infrastructure (Configuration)
```
src/main/java/com/sportygroup/liveevents/infrastructure/
├── config/         # Spring configuration
└── adapter/        # Spring service adapters
```
- **Configuration** wires dependencies
- **Adapters** bridge Spring framework with domain

## 🧠 Design Benefits

- ✅ **Dependency Inversion**: Core business logic depends only on abstractions
- ✅ **Testability**: Each layer can be tested independently with mocks
- ✅ **Flexibility**: Easy to swap implementations (e.g., database, message broker)
- ✅ **Clean Separation**: Business rules isolated from infrastructure concerns
- ✅ **SOLID Principles**: Single responsibility, open/closed, dependency inversion
- ✅ **Immutability**: Java 17 records for safe data transfer
- ✅ **Validation**: Strict input validation with @Validated annotations
- ✅ **Retry Logic**: Built-in @Retryable support for resilience
- ✅ **Observability**: Actuator endpoints for monitoring

---

## 🐳 Docker Setup

You can run the **Live Events Service** and required infrastructure using Docker.

---

### 🧱 Build Docker Image for This App

A `Dockerfile` is located in the project root.

```bash
docker build -t live-events-app .
```

### ▶ Run the App and Dependencies

A docker-compose.yml is also in the root. It starts the Spring Boot app along with any configured services.

```bash
# Run with default configuration
docker-compose -f docker-compose.yml up

# Run with custom Kafka server
KAFKA_SERVER_IP=172.27.11.4 docker-compose -f docker-compose.yml up
```

### ⚙ Kafka Only (Optional for Dev/Testing)

If you only want to bring up Kafka infrastructure, a separate Compose file is located at:
src/main/docker/kafka.yml

Run it with:
```bash
docker-compose -f src/main/docker/kafka.yml up
```

### 🔍 Testing Without Kafka

The application gracefully handles Kafka unavailability:
- **Integration tests** use mocked Kafka
- **Simulation** falls back to mock publisher
- **Spring Boot app** will show connection errors but continue running

## 📈 Actuator Endpoints

Spring Boot Actuator exposes:

- `GET /actuator/health` — Service health check
- `GET /actuator/metrics` — JVM and system metrics
- `GET /actuator/info` — Project metadata (if configured)


## ⚠ AI Use Disclosure

Some parts of this code were written or assisted by AI tools (e.g. ChatGPT) to improve productivity. All generated logic has been reviewed and validated.

---

## 📄 License

This project is provided for educational and evaluation purposes.