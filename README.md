# Food Ordering System (Kotlin)

This repository is a **Kotlin rewrite** of an event-driven microservices project, created to practice and learn Kotlin
while keeping the same architecture and module layout as the original Java codebase.

## Original Project

This project is based on the following Java implementation (Clean Architecture, DDD, Saga, Outbox, Kafka):

**[agelenler/food-ordering-system](https://github.com/agelenler/food-ordering-system)**

It comes from the Udemy
course [Microservices: Clean Architecture, DDD, SAGA, Outbox & Kafka](https://www.udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes/).
The original uses Spring Boot and Java with four microservices, covering Saga orchestration, the transactional Outbox
pattern, and Kafka-based event-driven communication.

## Overview

A **food ordering system** built as microservices with an event-driven flow:

1. **Customer Service** — customer management
2. **Order Service** — creates orders, orchestrates the Saga, coordinates payment and restaurant approval
3. **Payment Service** — handles payment requests and results
4. **Restaurant Service** — handles restaurant approval requests and results

Key patterns:

- **DDD** — domain-driven design with bounded contexts per service
- **Clean / Hexagonal Architecture** — separation of domain, application, and infrastructure layers
- **Saga** — distributed transaction orchestration (order → payment → restaurant approval)
- **Transactional Outbox** — reliable publishing of domain events to Kafka after DB commits
- **CQRS** (where applicable) — command/query separation

## Tech Stack

| Category  | Technology                               |
|-----------|------------------------------------------|
| Language  | Kotlin 2.1                               |
| Framework | Spring Boot 3.4                          |
| Build     | Maven (multi-module)                     |
| JDK       | 17                                       |
| Messaging | Apache Kafka + Confluent Avro Serializer |
| Database  | PostgreSQL                               |
| ORM       | Spring Data JPA / Hibernate 6            |

> **Note:** Kafka Avro models are **generated at build time** from `.avsc` schemas via `avro-maven-plugin` as **Java**
> classes. Kotlin modules import these generated types (a common approach in Kotlin + Avro projects).

## Project Structure

```
food-ordering-system-kotlin/
├── common/                 # Shared domain primitives
├── infrastructure/         # Kafka, Saga, Outbox, etc.
├── customer-service/
├── order-service/
├── payment-service/
└── restaurant-service/
```

Each service typically includes:

- `*-domain-core` — domain entities and domain services
- `*-application-service` — application services, Saga steps, Outbox schedulers
- `*-dataaccess` — JPA persistence
- `*-messaging` — Kafka producers and consumers
- `*-container` — Spring Boot entry point

## Build

```bash
# Ensure JDK 17
export JAVA_HOME=<path-to-jdk-17>
mvn -v

# Full build
mvn clean install
```

Generate Avro models only:

```bash
mvn generate-sources -pl infrastructure/kafka/kafka-model
```

- **Schemas:** `infrastructure/kafka/kafka-model/src/main/resources/avro/`
- **Generated output:** `infrastructure/kafka/kafka-model/target/generated-sources/avro/` (typically not committed)

## Run

Follow the same steps as the [original Java project](https://github.com/agelenler/food-ordering-system):

1. Install and start **PostgreSQL** (default port `5432`)
2. Start Zookeeper and Kafka with Docker Compose (see `infrastructure/docker-compose/` in the original repo; copy those
   files here if they are not included in this fork)
3. Run each service’s container module from your IDE or CLI:
    - `customer-service/customer-container`
    - `order-service/order-container`
    - `payment-service/payment-container`
    - `restaurant-service/restaurant-container`

### Default Ports

| Service    | Port |
|------------|------|
| Order      | 8181 |
| Payment    | 8182 |
| Restaurant | 8183 |
| Customer   | 8184 |

## Suggested Learning Path

Compare side-by-side with the [Java repository](https://github.com/agelenler/food-ordering-system) and work through
modules in this order:

1. `common/common-domain` — value objects, entities, exceptions
2. `customer-service` — smallest end-to-end microservice
3. `infrastructure/kafka` — Kafka config and messaging wrappers
4. `order-service` — Saga and Outbox core logic
5. `payment-service` / `restaurant-service` — Saga participants

## Acknowledgements

- Original project and course: [agelenler/food-ordering-system](https://github.com/agelenler/food-ordering-system)
-
Course: [Microservices: Clean Architecture, DDD, SAGA, Outbox & Kafka](https://www.udemy.com/course/microservices-clean-architecture-ddd-saga-outbox-kafka-kubernetes/)

## License

This project is licensed under the [MIT License](LICENSE) — Copyright (c)
2026 [Rurutia1027](https://github.com/Rurutia1027).

The original Java implementation remains the work of its respective authors;
see [agelenler/food-ordering-system](https://github.com/agelenler/food-ordering-system).
