# Architecture

## Overview

The AUCA Library Management System is a layered Java application backed by PostgreSQL and Hibernate ORM. There is no HTTP UI; behavior is validated through JUnit integration tests that exercise services against a real database.

## Layers

| Layer | Package | Responsibility |
|-------|---------|----------------|
| Domain | `com.auca.library.domain` | JPA entities and enums (User, Book, Borrower, Location, …) |
| DAO | `com.auca.library.dao` | Hibernate persistence for each aggregate |
| Service | `com.auca.library.service` | Business rules (borrow limits, late fees, location hierarchy) |
| Exception | `com.auca.library.exception` | Typed runtime errors (`EntityNotFoundException`, `BorrowLimitExceededException`, …) |
| Util | `com.auca.library.util` | `HibernateUtil` builds the shared `SessionFactory` |

Services receive a `SessionFactory` in their constructor (constructor injection friendly for tests).

## Data flow (borrow book)

1. `BorrowService.borrowBook` loads reader and book via DAOs.
2. Book must be `AVAILABLE`; membership borrow limit is enforced via `validateBorrowLimit`.
3. A `Borrower` row is created with 14-day due date and zero fine.
4. Book status becomes `BORROWED`; shelf stock counters are updated when applicable.

## Configuration

Database connection settings live in `src/main/resources/application.properties`. At runtime, `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables override file defaults (see `.env.example`).

## Quality gates

- **CI** (`.github/workflows/ci.yml`): Checkstyle, OWASP dependency-check, `mvn test` with PostgreSQL 16.
- **Local DB**: `docker compose up -d` starts PostgreSQL with credentials matching the defaults.
- **Coverage**: JaCoCo report generated on `mvn verify` under `target/site/jacoco`.
