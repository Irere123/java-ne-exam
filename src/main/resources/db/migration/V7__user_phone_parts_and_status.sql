-- ===================================================================
-- V7: Make the user phone two-part (country code + number) and give a
--     user an explicit Active/Inactive status, separate from the
--     `enabled` flag (which now means "email verified" only).
--   Portable DDL: PostgreSQL (runtime) and H2/PostgreSQL mode (tests).
-- ===================================================================
ALTER TABLE users ADD COLUMN country_code VARCHAR(5) NOT NULL DEFAULT '+250';
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
