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

## Email delivery (outbox dispatcher)

The triggers only *persist* a notification; emailing it is the application's job.
Since the rows are created at the database level, the app reacts to them with an
outbox-style poller rather than firing emails inline:

- Each notification carries an `email_sent` flag (migration `V8`, default `false`).
- `NotificationDispatchService` runs on a fixed schedule
  (`app.notifications.dispatch-interval-ms`, default 30s). Each run picks up the
  oldest undelivered notifications, emails each customer the stored `message`
  (subject derived from the notification `type`), and flips `email_sent` to `true`
  **only after the send succeeds**.
- Sending uses `EmailService.sendNotificationEmail(...)`, which never throws: a
  transient SMTP failure leaves `email_sent = false` so the notification is retried
  on the next run. This gives at-least-once delivery with idempotent marking — every
  notification is emailed exactly once under normal operation.
- When mail is disabled (`app.mail.enabled=false`) the body is logged and the
  notification is marked sent, so local runs without SMTP don't loop forever.

`GET /api/notifications` (and the customer's `/api/me/notifications`) expose the
`emailSent` flag so delivery status is visible.
