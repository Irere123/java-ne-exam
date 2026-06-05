# API reference

The full, interactive contract is published by Swagger UI at
<http://localhost:8080/swagger-ui.html> when the app is running. This page summarises
the roles, the endpoint surface, and the request-validation rules.

## Roles

| Role            | Responsibilities                                              |
|-----------------|--------------------------------------------------------------|
| `ROLE_ADMIN`    | Configure tariffs, approve bills, manage users, manage data  |
| `ROLE_OPERATOR` | Register customers/meters, capture meter readings            |
| `ROLE_FINANCE`  | Approve bills, record payments, apply penalties              |
| `ROLE_CUSTOMER` | View own bills, payment history, and notifications           |

Self-signup (`POST /api/auth/register`) creates a `CUSTOMER`. Staff accounts
(`OPERATOR`, `FINANCE`, and additional `ADMIN`s) are created by an admin via
`POST /api/admin/users`. A seed admin is created on first start
(`admin@java-exam.local` / `admin12345`, configurable via `app.admin.*`).

All endpoints require a valid JWT **except** `/api/auth/**` and the Swagger docs.
Fine-grained access is enforced per handler with `@PreAuthorize`.

## Endpoints

### Authentication — `/api/auth` (public)

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/register`            | Self-signup; creates a `CUSTOMER` and sends a verification email |
| `GET`  | `/verify`              | Confirm an email-verification token |
| `POST` | `/resend-verification` | Re-send the verification email |
| `POST` | `/login`               | Exchange credentials for access + refresh tokens |
| `POST` | `/refresh`             | Rotate the refresh token and issue a new access token |
| `POST` | `/logout`              | Deny-list the current refresh token |
| `POST` | `/forgot-password`     | Start a password reset (emails a reset token) |
| `POST` | `/reset-password`      | Complete a password reset |

### Account — `/api/account` (authenticated user)

| Method | Path | Purpose |
|--------|------|---------|
| `PUT`  | `/password`    | Change the current user's password |
| `POST` | `/logout-all`  | Invalidate all sessions (bumps the user's token version) |

### Admin users — `/api/admin/users` (ADMIN)

| Method  | Path | Purpose |
|---------|------|---------|
| `POST`  | `/`            | Create a staff user (OPERATOR / FINANCE / ADMIN) |
| `GET`   | `/`, `/{id}`   | List users / fetch one |
| `PATCH` | `/{id}/role`   | Change a user's role |
| `PATCH` | `/{id}/status` | Enable/disable a user |

### Customers — `/api/customers`

| Method  | Path | Purpose | Roles |
|---------|------|---------|-------|
| `POST`  | `/`            | Register a customer | ADMIN / OPERATOR |
| `PUT`   | `/{id}`        | Update a customer   | ADMIN / OPERATOR |
| `PATCH` | `/{id}/status` | Change status       | ADMIN / OPERATOR |
| `GET`   | `/`, `/{id}`   | List / fetch        | ADMIN / OPERATOR / FINANCE |

### Meters — `/api/meters`

| Method  | Path | Purpose | Roles |
|---------|------|---------|-------|
| `POST`  | `/`            | Register a meter | ADMIN / OPERATOR |
| `PATCH` | `/{id}/status` | Change status    | ADMIN / OPERATOR |
| `GET`   | `/`, `/{id}`   | List / fetch     | ADMIN / OPERATOR / FINANCE |

### Readings — `/api/readings`

| Method | Path | Purpose | Roles |
|--------|------|---------|-------|
| `POST` | `/`  | Capture a meter reading | OPERATOR / ADMIN |
| `GET`  | `/`  | List readings           | OPERATOR / ADMIN / FINANCE |

### Tariffs — `/api/tariffs`

| Method | Path | Purpose | Roles |
|--------|------|---------|-------|
| `POST` | `/`          | Create a tariff version | ADMIN |
| `GET`  | `/`, `/{id}` | List / fetch            | ADMIN / FINANCE |

### Bills — `/api/bills` (FINANCE / ADMIN)

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/generate`        | Generate a bill from a reading (`?readingId=`) |
| `POST` | `/{id}/approve`    | Approve a pending bill |
| `POST` | `/apply-penalties` | Run `sp_apply_late_penalties()` over overdue bills |
| `GET`  | `/`, `/{id}`       | List / fetch |

### Payments — `/api/payments` (FINANCE / ADMIN)

| Method | Path | Purpose |
|--------|------|---------|
| `POST` | `/`  | Record a (full or partial) payment against a bill |
| `GET`  | `/`  | List payments |

### Notifications — `/api/notifications` (ADMIN / FINANCE)

| Method | Path | Purpose |
|--------|------|---------|
| `GET`  | `/`  | List all notifications |

### Customer portal — `/api/me` (CUSTOMER)

| Method | Path | Purpose |
|--------|------|---------|
| `GET`  | `/bills`         | The signed-in customer's bills |
| `GET`  | `/payments`      | The signed-in customer's payments |
| `GET`  | `/notifications` | The signed-in customer's notifications |

## Request validation

Every date the API accepts is validated so a stored value can only ever make sense for
the system. Two layers enforce this.

**Field-level** — a reusable `@PlausibleDate` bean-validation constraint rejects
nonsense values with a `400` and a clear message before the request reaches the
service:

| Field | Rule |
|-------|------|
| `installationDate` (meter) | a real date, on/after the system epoch (2000-01-01), not in the future |
| `readingDate` (reading)    | a real date, on/after the epoch, not in the future |
| `paymentDate` (payment)    | a real date, on/after the epoch, not in the future |
| `effectiveFrom` (tariff)   | a real date, on/after the epoch, at most 5 years ahead |

**Cross-record** — rules that depend on other data are enforced in the services:

- a **reading** cannot pre-date the meter's `installationDate`;
- a **payment** cannot pre-date the day its bill was issued;
- a new **tariff** version's `effectiveFrom` must be *after* the current version's, so
  versions always move forward in time.
