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
- Kafka broker running locally (e.g. via Docker)
- (Optional) Swagger/OpenAPI UI for API testing

---

### 🔧 Build & Run

```bash
# Build the application
mvn clean install

# Run the app
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/live-events-0.0.1-SNAPSHOT.jar
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

To change the polling interval, update the `@Scheduled(fixedRate = 10000)` annotation in `PollingSchedulerService`.

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

## ✅ Example cURL

```bash
curl -X POST http://localhost:8080/events/status \
-H "Content-Type: application/json" \
-d '{"eventId": "match-123", "status": "live"}'
```

## 🧠 Design Notes

- ✅ Facade Pattern: Simplifies the interaction between polling and external systems (fetch + publish)
- ✅ Adapter Pattern: Clean boundary around infrastructure (Kafka, HTTP clients)
- ✅ Hexagonal Architecture (Ports & Adapters): Core domain logic is isolated from external dependencies
- ✅ Single Responsibility Principle (SRP): Scheduling, state tracking, publishing, and fetching are decoupled
- ✅ Retry Logic: Built-in @Retryable support and advanced RetryTemplate configuration
- ✅ Immutability: Java 17 record used for DTOs to ensure safe data transfer
- ✅ Validation: Uses @Validated, @NotBlank, and @Pattern to enforce strict input validation
- ✅ Dependency Injection: Enables testability and adherence to the Dependency Inversion Principle
- ✅ Test Strategy: Combines unit tests (Mockito + JUnit 5) and full integration test with @SpringBootTest
- ✅ Docker-Ready: Packaged with Docker and docker-compose for local orchestration
- ✅ Observability: Actuator endpoints exposed at /actuator/** including /health, /info, and /metrics

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
docker-compose -f docker-compose.yml up
```

### ⚙ Kafka Only (Optional for Dev/Testing)

If you only want to bring up Kafka infrastructure, a separate Compose file is located at:
src/main/docker/kafka.yml

Run it with:
```bash
docker-compose -f src/main/docker/kafka.yml up
```

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