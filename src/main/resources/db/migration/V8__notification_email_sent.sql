-- ===================================================================
-- V8: Track email delivery of customer notifications.
--   Notifications are written by database triggers; a background
--   dispatcher emails the ones not yet delivered and flips this flag,
--   so a transient mail failure is simply retried on the next run.
--   Portable DDL: PostgreSQL (runtime) and H2/PostgreSQL mode (tests).
-- ===================================================================
ALTER TABLE notifications ADD COLUMN email_sent BOOLEAN NOT NULL DEFAULT FALSE;

-- Speeds up the dispatcher's "find undelivered" query.
CREATE INDEX idx_notifications_email_sent ON notifications (email_sent);
