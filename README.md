# Utility Billing System

A secure, automated backend for a national utility company (WASAC / REG style) that
manages customers, meters, meter readings, postpaid billing, payments, and customer
notifications.

Built with **Spring Boot 4**, **Spring Data JPA**, **Spring Security (JWT)**,
**Flyway**, and **PostgreSQL**, with a fully documented **Swagger UI**.

---

## What it does

- **Customer & meter management** — register customers and their water/electricity
  meters, with duplicate protection on national ID and email.
- **Metered readings** — operators capture one reading per meter per month;
  consumption is computed and validated automatically.
- **Versioned tariffs** — tiered pricing per meter type, with service charge, VAT,
  and penalty rate; tariffs are versioned so prices always move forward in time.
- **Postpaid billing** — generate a bill from a reading, approve it, and settle it
  through full or partial payments.
- **Automated notifications** — PostgreSQL triggers raise a notification when a bill
  is generated and again when it is fully paid; a scheduled dispatcher then emails
  each one to the customer (retrying transient failures).
- **Late penalties** — a cursor-based stored procedure applies penalties to overdue,
  unpaid bills on demand.
- **Stateless JWT auth** — access + refresh tokens, role-based access control, email
  verification, and password reset.

---

## Tech stack

| Concern            | Choice                                              |
|--------------------|-----------------------------------------------------|
| Language / runtime | Java 17, Spring Boot 4.0.x                           |
| Persistence        | Spring Data JPA / Hibernate (`validate` mode)       |
| Database           | PostgreSQL (runtime), H2 in PostgreSQL mode (tests) |
| Migrations         | Flyway (`src/main/resources/db/migration`)          |
| Security           | Spring Security, stateless JWT (access + refresh)   |
| DB routines        | PL/pgSQL triggers + a cursor-based stored procedure |
| API docs           | springdoc OpenAPI / Swagger UI                      |

The schema is owned by Flyway and Hibernate runs in `validate` mode — it never
generates DDL — so the entity model and the migrations stay in lock-step.

---

## Quick start

**Prerequisites:** JDK 17+ and a local PostgreSQL with a database named `java_exam`.

```bash
createdb java_exam            # or create it with your DB tooling
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Flyway applies all migrations on startup and a seed admin account is created on first
run (`admin@java-exam.local` / `admin12345`).

- **Swagger UI:** <http://localhost:8080/swagger-ui.html>
- **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>

Run the tests (in-memory H2 — no PostgreSQL or SMTP required):

```bash
./mvnw test
```

See **[docs/configuration.md](docs/configuration.md)** for environment variables and
overrides.

---

## Project structure

```
java-exam/
├── README.md                  # you are here
├── docs/                      # detailed documentation (see below)
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/javaexam/
    │   │   ├── config/         # app + OpenAPI + scheduling config, data seeding
    │   │   ├── controllers/    # REST endpoints
    │   │   ├── dtos/           # request/response payloads
    │   │   ├── exceptions/     # ApiException + global handler
    │   │   ├── models/         # JPA entities, enums, shared response shapes
    │   │   ├── repositories/   # Spring Data JPA repositories
    │   │   ├── security/       # JWT filter, security config, auth DTOs
    │   │   ├── services/       # business logic
    │   │   ├── utils/          # date rules, validation patterns
    │   │   └── validation/     # @PlausibleDate constraint
    │   └── resources/
    │       ├── application.properties
    │       └── db/
    │           ├── migration/          # portable schema (PostgreSQL + H2)
    │           └── specific/{vendor}/  # vendor-only routines (PL/pgSQL vs H2 no-op)
    └── test/
```

---

## Documentation

Detailed docs live in the [`docs/`](docs/) folder:

| Document | What's inside |
|----------|---------------|
| [Architecture](docs/architecture.md)           | Layered design, request pipeline, and billing-lifecycle diagrams |
| [Data model](docs/data-model.md)               | Entity-relationship diagram and the constraints that enforce the rules |
| [Database routines](docs/database-routines.md) | Triggers, the cursor stored procedure, and Flyway vendor scoping |
| [API reference](docs/api-reference.md)         | Roles, the full endpoint surface, and request-validation rules |
| [Configuration](docs/configuration.md)         | Prerequisites, running, environment variables, and tests |
| [Walkthrough](docs/walkthrough.md)             | An end-to-end run through the API with Swagger or Postman |

---

## Scope notes

- **Postpaid billing** is implemented end-to-end for both water and electricity,
  reflecting the goal of unifying everything onto a postpaid model. Prepaid
  token-vending is intentionally out of scope and would be a future extension.
- Late penalties are applied on demand via the cursor-based stored procedure rather
  than a background scheduler, keeping the moving parts minimal.
