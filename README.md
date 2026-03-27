# User Management Microservice App with Kafka and Redis
A backend system built on a microservices architecture with communication via Apache Kafka. Implements user registration with email verification, pagination, and idempotent event handling.

## Features
- **Event-Driven Flow** — on user registration, a `UserCreatedEvent` is published to Kafka after transaction commit (`@TransactionalEventListener(AFTER_COMMIT)`), ensuring no phantom events on rollback
- **Idempotent Consumer** — noti-service deduplicates events by UUID before sending email, preventing duplicate notifications on Kafka redelivery
- **Email Verification** — on registration a `VerifyToken` is persisted; user activates account via a link sent to their email
- **Paginated User Listing** — supports page/size/sortBy/direction with validation of allowed sort fields
- **Virtual Threads** — uses Project Loom virtual threads for improved throughput under I/O load
- **Dev / Prod Profiles** — `dev` uses local Docker build + MailHog; `prod` pulls pre-built images from registry
- **Integration Tests** — Testcontainers spins up real PostgreSQL, Redis and Kafka for integration tests

## Tech Stackg
- **Java 25**
- **Spring Boot 4**
- **Postgres**
- **Liquidbase Migration**
- **MapStruct**
- **OpenAPI / Swagger**
- **Kafka**

### Prerequisites
- Docker
- Java 25

### Running with Docker
1. Clone the repository:
```bash
git clone https://github.com/MykiDevs/event-driven-user-management.git
cd event-driven-user-management
```
2. Copy .env.example to .env and adjust if needed:
```bash
cp .env.example .env
```
3. **Start all services:**
#### Production mode ( uses pre-built images from DockerHub ):
```bash
docker compose up 
```
#### Developement mode( only infrastructure ):
```bash
docker compose -f docker-compose-dev.yml up
```
## Running Tests
### user-service test
```bash
./gradlew :user-service:test
```
### noti-service test
```bash
./gradlew :noti-service:test
```
Integration tests require Docker (Testcontainers pulls PostgreSQL, Redis and Kafka automatically).
## API Reference (Dev profile)
http://localhost:8080/swagger-ui/index.html`
## Notes
- The current Kafka publishing uses `@TransactionalEventListener` + `@Async` as a lightweight alternative to a full outbox pattern. For production use, consider replacing with the Transactional Outbox Pattern.
- MailHog is only included in the dev/local setup. In real production, configure an actual SMTP provider via environment variables.
## License
[MIT](https://github.com/MykiDevs/event-driven-user-management/blob/main/LICENSE)
