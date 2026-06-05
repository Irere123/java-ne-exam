-- ===================================================================
-- V6: Link a customer to an optional self-service login account.
--   A customer MAY own exactly one user account (nullable, unique).
--   Staff users (ADMIN/OPERATOR/FINANCE) have no customer record.
--   Portable DDL: runs on PostgreSQL (runtime) and H2/PostgreSQL mode (tests).
-- ===================================================================
ALTER TABLE customers ADD COLUMN user_id BIGINT;
ALTER TABLE customers ADD CONSTRAINT uq_customers_user_id UNIQUE (user_id);
ALTER TABLE customers ADD CONSTRAINT fk_customers_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE SET NULL;
CREATE INDEX idx_customers_user_id ON customers (user_id);
