# Data model

The schema is owned by Flyway (`src/main/resources/db/migration`) and validated by
Hibernate at startup. The diagram below shows the core entities and how they relate;
the complete schema — including the auth-token table, enums, and audit timestamps — is
maintained as a machine-readable DBML file in [`erd.dbml`](erd.dbml) (paste it into
<https://dbdiagram.io> to render).

## Entity-relationship diagram

```mermaid
erDiagram
    USERS ||--o{ METER_READINGS : "records (operator)"
    USERS ||--o{ PAYMENTS : "records (finance)"
    USERS |o--o| CUSTOMERS : "self-service login (optional 1:1)"

    CUSTOMERS ||--o{ METERS : owns
    CUSTOMERS ||--o{ BILLS : "is billed"
    CUSTOMERS ||--o{ NOTIFICATIONS : receives

    METERS ||--o{ METER_READINGS : has
    METER_READINGS ||--|| BILLS : "billed as (1:1)"

    TARIFFS ||--o{ TARIFF_TIERS : "has consumption tiers"
    TARIFFS ||--o{ BILLS : "priced by"

    BILLS ||--o{ PAYMENTS : "settled by"
    BILLS ||--o{ NOTIFICATIONS : "triggers"

    USERS {
        bigint id PK
        string first_name
        string last_name
        string email UK
        string country_code
        string phone_number
        string password
        string role
        boolean enabled "email verified"
        string status "ACTIVE / INACTIVE"
        int token_version
    }
    CUSTOMERS {
        bigint id PK
        string full_name
        string national_id UK
        string email UK
        string phone_number
        string address
        string status
        bigint user_id FK "nullable, unique"
    }
    METERS {
        bigint id PK
        string meter_number UK
        string meter_type
        date installation_date
        string status
        bigint customer_id FK
    }
    METER_READINGS {
        bigint id PK
        bigint meter_id FK
        numeric previous_reading
        numeric current_reading
        numeric consumption
        date reading_date
        int reading_year
        int reading_month
        bigint recorded_by FK
    }
    TARIFFS {
        bigint id PK
        string meter_type
        int version
        numeric service_charge
        numeric vat_rate
        numeric penalty_rate
        date effective_from
    }
    TARIFF_TIERS {
        bigint id PK
        bigint tariff_id FK
        numeric min_units
        numeric max_units
        numeric rate_per_unit
    }
    BILLS {
        bigint id PK
        string bill_number UK
        bigint customer_id FK
        bigint meter_id FK
        bigint reading_id FK,UK
        bigint tariff_id FK
        int billing_year
        int billing_month
        numeric consumption
        numeric consumption_charge
        numeric service_charge
        numeric tax_amount
        numeric penalty_amount
        numeric total_amount
        numeric amount_paid
        numeric outstanding_balance
        string status
        date due_date
    }
    PAYMENTS {
        bigint id PK
        string payment_reference UK
        bigint bill_id FK
        numeric amount
        string method
        date payment_date
        bigint recorded_by FK
    }
    NOTIFICATIONS {
        bigint id PK
        bigint customer_id FK
        bigint bill_id FK
        string type
        string message
        timestamp created_at
    }
```

## Constraints that enforce the rules

The business rules are backed by database constraints, so they hold regardless of how
data is inserted:

| Constraint | Effect |
|------------|--------|
| `customers.national_id` unique, `customers.email` unique | Prevents duplicate customer registration |
| `meter_readings (meter_id, reading_year, reading_month)` unique | One reading per meter per month/year |
| `tariffs (meter_type, version)` unique | Tariffs are versioned per meter type |
| `bills.reading_id` unique | At most one bill per reading |
| `customers.user_id` nullable + unique | Optional 1:1 link to a self-service login |

## Customer ↔ user link

A `CUSTOMER` record (the billed party) is distinct from a `USER` (a login). They are
optionally linked 1:1 via `customers.user_id`: when a customer self-registers — or
when an admin creates a customer whose email matches an existing user — the two are
auto-linked. That link is what powers the customer portal (`/api/me/**`). An account
with no linked customer profile gets a clear `404`.
