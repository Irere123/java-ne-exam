# Database routines & notifications

Notifications and late-penalty handling are implemented in the database itself, using
PL/pgSQL triggers and a cursor-based stored procedure. Because PL/pgSQL is
PostgreSQL-specific, these routines are kept separate from the portable schema.

## Flyway vendor scoping

Flyway scans two locations (configured in `application.properties`):

```
spring.flyway.locations=classpath:db/migration,classpath:db/specific/{vendor}
```

| Location | Contents |
|----------|----------|
| `db/migration`          | Portable DDL — runs on both PostgreSQL and H2 |
| `db/specific/{vendor}`  | Vendor-only routines |

At runtime `{vendor}` resolves to `postgresql`; under the H2 context-load test it
resolves to `h2`. The PL/pgSQL routines live in
`db/specific/postgresql/V5__billing_routines.sql`, while
`db/specific/h2/V5__billing_routines.sql` is a no-op placeholder. Keeping them
*outside* `db/migration` ensures Flyway's recursive scan never tries to load both on
the same database.

## What `V5__billing_routines.sql` defines

1. **Trigger `trg_bill_after_insert`** — on every new bill, inserts a
   `BILL_GENERATED` notification using the required message format:

   ```
   Dear <CustomerName>,
   Your <Month/Year> utility bill of <Amount> FRW has been successfully processed.
   ```

2. **Trigger `trg_bill_after_update`** — when a bill transitions to `PAID`, inserts a
   `PAYMENT_COMPLETED` notification for the customer.

3. **Stored procedure `sp_apply_late_penalties()`** — uses an explicit **cursor** to
   walk overdue, unpaid bills and add the tariff's penalty, moving each to `OVERDUE`.
   It is invoked on demand via `POST /api/bills/apply-penalties`.

Because these transitions happen in the database, the notification rows are created
atomically with the bill change they describe — there is no application code path
that can generate a bill or complete a payment without the matching notification.
