-- ===================================================================
-- V5 (H2 / tests): no-op.
--
-- The Task 6 database routines are written in PL/pgSQL and live in
-- db/specific/postgresql/V5__billing_routines.sql. H2 (used only for the
-- context-load test) cannot parse PL/pgSQL, so this placeholder keeps the
-- migration version line in sync without creating any routine. Notifications
-- and late penalties are therefore exercised against PostgreSQL at runtime.
-- ===================================================================
SELECT 1;
