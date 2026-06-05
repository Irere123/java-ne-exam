# Configuration & running

## Prerequisites

- **JDK 17+**
- **PostgreSQL** running locally with a database named `java_exam`

```bash
createdb java_exam            # or create it with your DB tooling
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

Flyway applies all migrations on startup. A seed admin account is created on first run
if it does not already exist (`admin@java-exam.local` / `admin12345`).

- **Swagger UI:** <http://localhost:8080/swagger-ui.html>
- **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>

## Tests

The test suite uses an in-memory H2 database in PostgreSQL-compatibility mode, so no
PostgreSQL or SMTP server is required:

```bash
./mvnw test
```

## Environment variables

All settings have sensible defaults (see `src/main/resources/application.properties`)
and can be overridden via environment variables. The defaults are convenient for local
development; **set real values before deploying anywhere shared.**

### Database

| Variable | Default | Purpose |
|----------|---------|---------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/java_exam` | JDBC URL |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |

### Security (JWT)

| Variable | Default | Purpose |
|----------|---------|---------|
| `JWT_SECRET`                  | _(dev value)_ | Base64-encoded signing key, **≥ 256 bits**. Generate with `openssl rand -base64 32` |
| `JWT_ACCESS_EXPIRATION_MS`    | `900000` (15 min) | Access-token lifetime |
| `JWT_REFRESH_EXPIRATION_MS`   | `604800000` (7 days) | Refresh-token lifetime (rotated on every `/refresh`) |

### Mail (SMTP)

| Variable | Default | Purpose |
|----------|---------|---------|
| `MAIL_HOST`     | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT`     | `587` | SMTP port |
| `MAIL_USERNAME` | _(dev value)_ | SMTP user |
| `MAIL_PASSWORD` | _(dev value)_ | SMTP password / app password |
| `MAIL_FROM`     | `no-reply@java-exam.local` | "From" address on outgoing mail |
| `APP_BASE_URL`  | `http://localhost:8080` | Base URL used to build email-verification links |

### Tokens & cleanup

| Variable | Default | Purpose |
|----------|---------|---------|
| `VERIFICATION_EXPIRATION_MINUTES`   | `1440` (24 h) | Email-verification token lifetime |
| `PASSWORD_RESET_EXPIRATION_MINUTES` | `60` (1 h) | Password-reset token lifetime |
| `CLEANUP_INTERVAL_MS`               | `3600000` (1 h) | How often expired tokens are purged |

### Seed admin

| Variable | Default |
|----------|---------|
| `ADMIN_EMAIL`      | `admin@java-exam.local` |
| `ADMIN_PASSWORD`   | `admin12345` |
| `ADMIN_FIRST_NAME` | `System` |
| `ADMIN_LAST_NAME`  | `Administrator` |
| `ADMIN_PHONE`      | `788000000` |
