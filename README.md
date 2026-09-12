# AUCA Library Management System

[![CI](https://github.com/Habimana06/finalExam_testing_26432/actions/workflows/ci.yml/badge.svg)](https://github.com/Habimana06/finalExam_testing_26432/actions/workflows/ci.yml)

Student final project for the course **Software Testing and Techniques**.

The system manages real (hard copy) books in the library. It tracks who borrowed a book, membership limits, due dates, and late return fees.

No user interface is included. Data is inserted and verified using JUnit 4 tests with PostgreSQL.

---

## Technologies used

- Java 21
- Maven
- Hibernate ORM 6.5
- PostgreSQL (`auca_library_db`)
- JUnit 4

Main package: `com.auca.library`

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for layer responsibilities and data flow.

---

## Folder structure

```
com.auca.library
 ├── domain      // entities and enums from the class diagram
 ├── dao          // Hibernate database operations
 ├── service      // business logic
 ├── util         // HibernateUtil
 └── exception   // custom exceptions
```

Note: `Person` is a superclass of `User` using `@MappedSuperclass`.  
Person is **not** created as a table in the database.

---

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (for local PostgreSQL)

---

## How to setup (fresh clone)

**Step 1 — start PostgreSQL**

```bash
docker compose up -d
```

This creates `auca_library_db` with user `postgres` / password `postgres` (see `.env.example` to customize).

**Step 2 — run tests**

```bash
mvn test
```

Optional: copy `.env.example` to `.env` and export variables if you use non-default credentials:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/auca_library_db
export DB_USER=postgres
export DB_PASSWORD=postgres
mvn test
```

**Full verify** (Checkstyle, dependency audit, tests, JaCoCo report):

```bash
mvn verify
```

Or use the Makefile:

```bash
make up
make verify
```

---

## Membership types

| Membership | Price per late day | Max books allowed |
|------------|--------------------|-------------------|
| Gold | 50 Rwf | 5 |
| Silver | 30 Rwf | 3 |
| Striver | 10 Rwf | 2 |

When a book is borrowed, the fine starts at 0.  
If the book is returned late, fee = number of late days × membership daily price.  
Normal loan period is 14 days.

---

## What was implemented

1. Create location hierarchy (Province, District, Sector, Cell, Village)
2. Find province name using village id
3. Find province name using person id
4. User authentication (username and password)
5. Register membership (status starts as PENDING)
6. Borrow a book
7. Check membership borrow limit
8. Assign book to a shelf
9. Assign shelf to a room
10. Count books in a room
11. Find the room with the fewest books
12. Calculate late return fees

---

## Class diagram relationships

- User inherits from Person
- A user lives in a village (Location)
- Location uses parent_id for Province → District → Sector → Cell → Village
- A user can have memberships and borrow records
- A book is kept on a shelf
- A shelf is located in a room

---

## Test classes

| Test class | What it tests |
|------------|---------------|
| LocationServiceTest | locations and province lookup |
| UserServiceTest | login / authentication |
| MembershipServiceTest | membership registration |
| BorrowServiceTest | borrowing a book |
| BorrowLimitTest | max books per membership |
| LateFeeTest | late return charges |
| BookServiceTest | assign book to shelf |
| RoomServiceTest | shelf/room and book counts |

All tests live under `src/test/java/com/auca/library` and run in CI on every push.

---

## Student information

- Student ID: 26432
- Repository: https://github.com/Habimana06/finalExam_testing_26432
- Course: Software Testing and Techniques (AUCA)
