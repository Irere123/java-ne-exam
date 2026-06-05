# Postman collection

`java-exam.postman_collection.json` exercises the full Utility Billing & Metering API
end to end, covering authentication, admin user management, tariffs, customers,
meters, readings, bills, payments, notifications, and the customer self-service portal.

## Run it

1. Start the app (PostgreSQL must be up — see the project README). It listens on
   `http://localhost:8080`. The seed admin (`admin@java-exam.local` / `admin12345`)
   is created automatically on first start.
2. In Postman: **Import** → select `java-exam.postman_collection.json`.
3. Run **either** way:
   - **Collection Runner** (recommended): select the collection and **Run**. The
     requests are ordered to run top-to-bottom and chain automatically.
   - **Manually**: start with *1. Authentication → Login as Admin*, then work down
     the folders. Each login/create step stores tokens and IDs as collection
     variables for the requests that follow.

No environment file is needed — `baseUrl`, the seed-admin credentials, and passwords
are baked in as collection variables (override `baseUrl` if the app runs elsewhere).

## What the happy path does

`Login as Admin` → create operator/finance/customer accounts → configure a tariff →
register a customer + meter → capture a reading → generate & approve a bill → record a
partial then final payment (bill becomes `PAID`) → view it all as the customer via
`/api/me/*`.

Each run generates a fresh, unique customer email + national ID (derived from a
timestamp set at `Login as Admin`), so the collection can be re-run against the same
database without duplicate-key conflicts.

## Notes

- **Tokens & IDs are captured automatically** by each request's test script — you
  don't paste anything by hand for the automated flow.
- **Roles:** the collection's default auth uses the admin token; operator/finance/
  customer requests override it with their own captured tokens, so role-based access
  is genuinely exercised (e.g. the operator captures readings, finance handles billing).
- **Manual-only requests:** *Verify Email* and *Reset Password* need a real token from
  the email that the server sends. Paste it into the `verificationToken` / `resetToken`
  variable before running them. They have no assertions and are skipped logically by the
  rest of the flow.
- **PostgreSQL features:** *Apply Late Penalties* (a stored procedure) and the
  trigger-generated notifications only populate on PostgreSQL, which is the app's
  configured datasource.
