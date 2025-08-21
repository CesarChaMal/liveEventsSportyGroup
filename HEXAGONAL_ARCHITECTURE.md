# 🏗️ Hexagonal Architecture Guide

## 📖 What is Hexagonal Architecture?

**Hexagonal Architecture** (also known as **Ports and Adapters**) is a software design pattern that isolates the core business logic from external concerns like databases, web frameworks, and messaging systems.

### 🎯 Core Principles:

1. **Business Logic at the Center**: Domain logic is independent of external systems
2. **Dependency Inversion**: Core depends on abstractions, not implementations
3. **Ports**: Define contracts (interfaces) for external interactions
4. **Adapters**: Implement ports to connect with specific technologies
5. **Testability**: Each layer can be tested independently

---

## 🏛️ Architecture Layers

### 🎯 Domain Layer (The Hexagon Core)
**Location**: `src/main/java/com/sportygroup/liveevents/domain/`

The **heart** of the application containing pure business logic.

#### 📁 `domain/model/`
**Contains**: Value objects and entities
- `EventId.java` - Immutable value object representing event identifiers
- `EventStatus.java` - Enum for event states (LIVE, ENDED)
- `Event.java` - Domain entity combining EventId and EventStatus
- `Score.java` - Value object for score data

**Why here?** These are core business concepts that exist regardless of technology choices.

#### 📁 `domain/service/`
**Contains**: Pure business logic services
- `DomainEventTrackingService.java` - Business rules for event lifecycle management

**Why here?** Contains business rules that don't fit naturally in entities but are still pure domain logic.

#### 📁 `domain/port/`
**Contains**: Interfaces defining contracts with external systems
- `EventRepository.java` - Contract for event persistence
- `ScoreFetcher.java` - Contract for fetching scores from external systems
- `EventPublisherPort.java` - Contract for publishing events

**Why here?** These are **ports** - they define WHAT the domain needs from the outside world, not HOW it's implemented.

---

### 🔄 Application Layer (Use Cases)
**Location**: `src/main/java/com/sportygroup/liveevents/application/`

Orchestrates domain operations and defines application-specific workflows.

#### 📁 `application/usecase/`
**Contains**: Application use cases that orchestrate domain operations
- `UpdateEventStatusUseCase.java` - Handles event status updates
- `ProcessLiveEventsUseCase.java` - Orchestrates live event processing

**Why here?** These represent specific application workflows that combine domain services to achieve business goals.

#### 📁 `application/dto/`
**Contains**: Data Transfer Objects for communication between layers
- `EventStatusRequest.java` - Input data structure for status updates
- `ScoreResponse.java` - Output data structure for score information

**Why here?** DTOs belong to the application layer as they define the contract between the application and external systems.

---

### ⬇️ Inbound Adapters (Left Side of Hexagon)
**Location**: `src/main/java/com/sportygroup/liveevents/in/`

Handle incoming requests and trigger application use cases.

#### 📁 `in/web/`
**Contains**: REST API controllers
- `EventStatusController.java` - Handles HTTP requests for event status

**Why here?** These are **inbound adapters** that convert HTTP requests into use case calls.

#### 📁 `in/scheduler/`
**Contains**: Scheduled task adapters
- `PollingSchedulerAdapter.java` - Triggers periodic event processing

**Why here?** Schedulers are another form of inbound adapter that trigger use cases based on time.

---

### ⬆️ Outbound Adapters (Right Side of Hexagon)
**Location**: `src/main/java/com/sportygroup/liveevents/out/`

Implement domain ports to connect with external systems.

#### 📁 `out/persistence/`
**Contains**: Database and storage implementations
- `InMemoryEventRepository.java` - In-memory implementation of EventRepository port

**Why here?** This is an **outbound adapter** that implements the EventRepository port using in-memory storage.

#### 📁 `out/external/`
**Contains**: External API clients
- `RestScoreFetcher.java` - HTTP client implementation of ScoreFetcher port

**Why here?** This adapter implements the ScoreFetcher port using REST API calls.

#### 📁 `out/messaging/`
**Contains**: Message publishing implementations
- `KafkaEventPublisherAdapter.java` - Kafka implementation of EventPublisherPort
- `MessagePublisherService.java` - Kafka publishing service

**Why here?** These adapters implement messaging ports using Kafka technology.

---

### ⚙️ Infrastructure Layer
**Location**: `src/main/java/com/sportygroup/liveevents/infrastructure/`

Provides configuration and framework integration.

#### 📁 `infrastructure/config/`
**Contains**: Spring Boot configuration classes
- `HexagonalConfig.java` - Wires hexagonal components together
- `AppConfig.java` - General application configuration
- `KafkaConfig.java` - Kafka-specific configuration
- `SwaggerConfig.java` - API documentation configuration

**Why here?** Configuration classes belong to infrastructure as they wire together the hexagonal architecture.

#### 📁 `infrastructure/adapter/`
**Contains**: Framework-specific service adapters
- `EventTrackingService.java` - Spring service adapter
- `PollingSchedulerService.java` - Spring scheduler adapter

**Why here?** These are infrastructure adapters that bridge Spring framework with the hexagonal core.

---

## 🔄 Data Flow Example

### Incoming Request Flow:
1. **HTTP Request** → `EventStatusController` (inbound adapter)
2. **Controller** → `UpdateEventStatusUseCase` (application layer)
3. **Use Case** → `DomainEventTrackingService` (domain service)
4. **Domain Service** → `EventRepository` port (domain port)
5. **Port** → `InMemoryEventRepository` (outbound adapter)

### Scheduled Processing Flow:
1. **Timer** → `PollingSchedulerAdapter` (inbound adapter)
2. **Scheduler** → `ProcessLiveEventsUseCase` (application layer)
3. **Use Case** → `ScoreFetcher` port (domain port)
4. **Port** → `RestScoreFetcher` (outbound adapter)
5. **Use Case** → `EventPublisherPort` (domain port)
6. **Port** → `KafkaEventPublisherAdapter` (outbound adapter)

---

## ✅ Benefits of This Structure

### 🧪 **Testability**
- **Domain**: Test pure business logic without external dependencies
- **Use Cases**: Mock ports to test application workflows
- **Adapters**: Test each adapter independently

### 🔄 **Flexibility**
- Swap `InMemoryEventRepository` for `DatabaseEventRepository` without changing domain
- Replace `RestScoreFetcher` with `GrpcScoreFetcher` without affecting use cases
- Change from Kafka to RabbitMQ by implementing new messaging adapter

### 🎯 **Clean Dependencies**
- Domain layer has **zero** external dependencies
- Application layer depends only on domain
- Adapters depend on application/domain but not on each other

### 🛡️ **Business Logic Protection**
- Core business rules are isolated from framework changes
- Database schema changes don't affect domain models
- API changes don't impact business logic

---

## 🚫 What NOT to Put Where

### ❌ **Don't put in Domain**:
- Spring annotations (`@Service`, `@Component`)
- Database entities (JPA `@Entity`)
- HTTP-specific code (`@RestController`)
- Framework-specific imports

### ❌ **Don't put in Application**:
- Database connection logic
- HTTP request/response handling
- Kafka producer configuration
- Framework-specific annotations (except configuration)

### ❌ **Don't put in Adapters**:
- Business logic or business rules
- Domain model validation
- Use case orchestration
- Cross-cutting business concerns

---

## 🎯 Summary

The hexagonal architecture ensures that:
- **Business logic** remains pure and testable
- **External systems** can be easily swapped
- **Dependencies** flow inward toward the domain
- **Testing** is straightforward with clear boundaries
- **Maintenance** is easier with separated concerns

Each folder serves a specific architectural purpose, creating a maintainable and flexible system that can evolve with changing requirements.