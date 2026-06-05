# Architecture

The application follows a conventional layered Spring Boot design. Each HTTP request
flows through a JWT security filter, a controller, a transactional service, and a JPA
repository before reaching PostgreSQL, where triggers and a stored procedure handle
the database-side automation.

## Layers

| Layer | Responsibility |
|-------|----------------|
| **Controller**  | Maps HTTP requests to actions, validates payloads, returns DTOs |
| **Service**     | Business logic, transactions (`@Transactional`), cross-record rules |
| **Repository**  | Spring Data JPA data access |
| **Model**       | JPA entities, enums, and shared response shapes |
| **Security**    | Stateless JWT filter, `SecurityFilterChain`, `@PreAuthorize` checks |
| **Database**    | Schema + PL/pgSQL triggers and the late-penalty stored procedure |

## Request pipeline

Every request carries a Bearer JWT (except `/api/auth/**` and the Swagger docs). The
filter authenticates it, the security layer authorizes the role, and any error is
funnelled through a single global handler that returns a consistent error shape.

```mermaid
flowchart LR
    Client -->|HTTP + Bearer JWT| Filter[JwtAuthenticationFilter]
    Filter -->|valid token| SC[SecurityFilterChain / @PreAuthorize]
    Filter -->|invalid| E401[401 Unauthorized]
    SC -->|authorized| Ctrl[Controller]
    SC -->|wrong role| E403[403 Forbidden]
    Ctrl --> Svc[Service @Transactional]
    Svc --> Repo[JPA Repository]
    Repo --> DB[(PostgreSQL + triggers/procedure)]
    DB --> Repo --> Svc --> Ctrl -->|JSON DTO| Client
    Svc -. throws .-> GEH[GlobalExceptionHandler] -->|ErrorResponse| Client
```

## Billing lifecycle

The core domain flow runs from a captured reading through to a settled, fully-paid
bill. Validation happens at each step, and two of the transitions are automated by
database triggers (see [Database routines](database-routines.md)).

```mermaid
flowchart TD
    A[OPERATOR captures reading] --> B{current > previous?\nmeter active?\none per month?}
    B -- no --> X[400 / 409]
    B -- yes --> C[Reading stored]
    C --> D[FINANCE/ADMIN generates bill]
    D --> E{customer active?\ntariff effective?}
    E -- no --> X
    E -- yes --> F[Bill PENDING\n consumption x tiers + service + VAT]
    F -->|DB INSERT trigger| N1[(BILL_GENERATED notification)]
    F --> G[FINANCE/ADMIN approves -> APPROVED]
    G --> H[FINANCE records payment]
    H --> I{outstanding == 0?}
    I -- no --> J[PARTIALLY_PAID]
    I -- yes --> K[PAID]
    K -->|DB UPDATE trigger| N2[(PAYMENT_COMPLETED notification)]
    G -. overdue & unpaid .-> P[sp_apply_late_penalties cursor -> OVERDUE]
```

For a hands-on run through this flow, see the [walkthrough](walkthrough.md).
