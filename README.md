# AUCA Library Management System

Final project for **Software Testing and Techniques**.

This system manages hard-copy books in the AUCA library: who borrows a book, how many books a member can borrow, when the book should be returned, and late return fees.

There is **no UI**. Everything is checked with **JUnit 4** test cases that insert real data into PostgreSQL.

---

## Tech stack

- Java 21
- Maven
- Hibernate ORM 6.5
- PostgreSQL database: `auca_library_db`
- JUnit 4

Package: `com.auca.library`

---

## Project folders

```
com.auca.library
 ├── domain      entities and enums
 ├── dao          database access (Hibernate)
 ├── service      business rules
 ├── util         HibernateUtil
 └── exception   BorrowLimitExceededException
```

`Person` is the superclass of `User`. It uses `@MappedSuperclass`, so **Person is not a table** in the database.

---

## Setup

### 1. Create the database

In PostgreSQL:

```sql
CREATE DATABASE auca_library_db;
```

### 2. Check database login

File: `src/main/resources/application.properties`

```properties
hibernate.connection.url=jdbc:postgresql://localhost:5432/auca_library_db
hibernate.connection.username=postgres
hibernate.connection.password=your_password
hibernate.hbm2ddl.auto=update
```

Tests use this same file (no separate test properties file).

### 3. Run the project tests

```bash
mvn test
```

Compile only:

```bash
mvn compile
```

---

## Membership rules

| Type | Late fee per day | Max books |
|------|------------------|-----------|
| Gold | 50 Rwf | 5 |
| Silver | 30 Rwf | 3 |
| Striver | 10 Rwf | 2 |

- Fine starts at **0** when borrowing
- Late fee = days late × membership daily rate
- Loan period = 14 days

---

## Requirements covered

1. Create locations (Province → District → Sector → Cell → Village)
2. Get province name by village id
3. Get province name by person id
4. Authenticate user
5. Register membership (starts as PENDING)
6. Borrow a book
7. Validate membership borrow limit
8. Assign book to shelf
9. Assign shelf to room
10. Count books in a room
11. Find room with fewest books
12. Calculate late return fees

---

## Relationships (from class diagram)

- User **IS** a Person
- User lives in a Village (Location)
- Location has parent (province down to village)
- User can have many Membership and Borrower records
- Book is on a Shelf; Shelf is in a Room
- Borrower links Reader (User) and Book

---

## How to explain this project

1. Domain classes match the UML diagram
2. DAO classes talk to PostgreSQL with Hibernate
3. Service classes contain the business rules
4. JUnit tests insert sample data and check each requirement method

---

## Author

Student ID: 26432  
GitHub: https://github.com/Habimana06/finalExam_testing_26432  
Course: Software Testing and Techniques — AUCA
