# AUCA Library Management System

Final project for **Software Testing and Techniques** — AUCA Library Management System.

This app helps the library manage hard-copy books: who borrows them, how many books a member can take, when books are due, and late return fees.

There is **no UI**. Data is inserted and checked through **JUnit 4 test cases**.

---

## Tech stack

| Tool | Version / notes |
|------|------------------|
| Java | 21 |
| Build | Maven |
| ORM | Hibernate 6.5 |
| Database | PostgreSQL (`auca_library_db`) |
| Tests | JUnit 4 |

Package root: `com.auca.library`

---

## Project structure

```
com.auca.library
 ├── domain      → entities and enums (Person, User, Book, Location, ...)
 ├── dao          → Hibernate save / find methods
 ├── service      → business logic (borrow, membership, fees, ...)
 ├── util         → HibernateUtil
 └── exception   → BorrowLimitExceededException
```

**Important:** `Person` is a **superclass** of `User`. It uses `@MappedSuperclass`, so it is **not** a table in the database.

---

## Database setup

1. Make sure PostgreSQL is running.
2. Create the database:

```sql
CREATE DATABASE auca_library_db;
```

3. Put your username and password in both files:

- `src/main/resources/application.properties`
- `src/test/resources/application-test.properties`

Example:

```properties
hibernate.connection.url=jdbc:postgresql://localhost:5432/auca_library_db
hibernate.connection.username=auca
hibernate.connection.password=auca123
hibernate.hbm2ddl.auto=update
```

Tables are created automatically by Hibernate when the app or tests start.

---

## Membership types

| Type | Daily late fee | Max books |
|------|----------------|-----------|
| Gold | 50 Rwf | 5 |
| Silver | 30 Rwf | 3 |
| Striver | 10 Rwf | 2 |

- Fine starts at **0** when a book is borrowed.
- Late fee = **days late × membership daily rate**.

---

## Main features (requirements)

1. Create location hierarchy (Province → District → Sector → Cell → Village)
2. Get province name from a village id
3. Get province name from a person id
4. Authenticate a user (username + password)
5. Register for membership (status starts as PENDING)
6. Borrow a book (fine = 0, status → BORROWED, due date set)
7. Validate membership borrow limit
8. Assign a book to a shelf
9. Assign a shelf to a room
10. Count books in a room
11. Find the room with the fewest books
12. Calculate late return fees

---

## How to run tests

From the project folder:

```bash
mvn test
```

To compile only:

```bash
mvn compile
```

---

## How relationships work (from the class diagram)

- **User IS a Person** (inheritance)
- User lives in a **Village** (Location)
- Location has a **parent** (province → … → village)
- User can have many **Membership** and **Borrower** records
- Book is on a **Shelf**, Shelf is in a **Room**
- Borrower links a **User** (reader) and a **Book**

---

## Author

Student project — AUCA Library Management System  
Course: Software Testing and Techniques
