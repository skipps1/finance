# Finance Tracker API

A portfolio REST API for personal finance tracking. The project demonstrates how I design a layered Java backend with secure authentication, role-based access control, relational data modelling, validation, and integration testing.

## What It Does

- Registers users and authenticates them with JSON Web Tokens (JWT).
- Lets each user manage their profile, password, budgets, and transactions.
- Tracks income and expenses with optional categories.
- Produces monthly income, expense, balance, and spending-by-category summaries.
- Restricts category creation, editing, and deletion to administrators.
- Prevents users from reading, updating, or deleting another user's budgets and transactions.

## Technologies

- **Java 21**
- **Spring Boot 4**
- **Spring Web MVC** for REST endpoints
- **Spring Security** with stateless JWT authentication and role-based authorization
- **JJWT** for token creation and validation
- **Spring Data JPA / Hibernate** for persistence
- **PostgreSQL** for relational data storage
- **Jakarta Bean Validation** for request validation
- **Maven** for dependency and build management
- **JUnit 5, MockMvc, and Spring Boot Test** for controller integration tests
- **Lombok** to reduce entity boilerplate

## Architecture

The codebase follows a layered structure:

```text
controller/   HTTP request handling and response mapping
service/      Business rules and ownership checks
repository/   Spring Data JPA queries and persistence
model/        JPA entities and domain enums
dto/          Validated request and response contracts
security/     JWT filter, token utility, and Spring Security configuration
exception/    Centralized API error handling
```

## Security

- Passwords are hashed with **BCrypt**.
- Login and registration are public; all other API endpoints require a valid Bearer token.
- Authentication is stateless: the JWT is sent as `Authorization: Bearer <token>`.
- `USER` and `ADMIN` roles are represented as Spring Security authorities.
- Only `ADMIN` users may create, update, or delete categories.
- Budget and transaction queries are scoped to the authenticated user, enforcing resource ownership at the database-query level.

## API Endpoints

| Area | Endpoints |
| --- | --- |
| Authentication | `POST /api/auth/register`, `POST /api/auth/login` |
| User account | `PUT /api/user/username`, `PUT /api/user/email`, `PUT /api/user/password`, `DELETE /api/user` |
| Transactions | `GET`, `POST /api/transaction`; `PUT`, `DELETE /api/transaction/{transactionId}` |
| Budgets | `GET`, `POST /api/budget`; `PUT`, `DELETE /api/budget/{budgetId}` |
| Categories | `GET /api/category`; `POST`, `PUT`, `DELETE /api/category/**` (admin only) |
| Reports | `GET /api/summary/monthly`, `/income`, `/expense`, `/expenses/by-category` |

Summary endpoints accept `year` and `month` query parameters, for example:

```text
GET /api/summary/monthly?year=2026&month=9
```

## Local Setup

### Prerequisites

- Java 21 or newer
- PostgreSQL

Create a database and set the required environment variables. The application reads credentials and the JWT signing secret from the environment rather than committing production secrets.

```bash
createdb finance_tracker

export FINANCE_DB_USERNAME=your_postgres_user
export FINANCE_DB_PASSWORD=your_postgres_password
export FINANCE_JWT_SECRET=use-a-random-secret-at-least-32-characters-long
```

Start the API:

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080` by default. Hibernate manages the application schema using `spring.jpa.hibernate.ddl-auto=update`.

## Testing

The project contains **29 controller integration tests** covering registration and login, validation failures, authorization rules, user updates, CRUD workflows, ownership checks, and financial summaries.

Tests use the `test` Spring profile and a separate PostgreSQL database configured in `src/test/resources/application-test.properties`.

```bash
./mvnw test
```

## Project Purpose

This is a CV and portfolio project built to demonstrate practical backend development skills: designing RESTful APIs, applying Spring Security, modelling data with JPA, working with PostgreSQL, enforcing user-level access boundaries, and validating the application through integration tests.
