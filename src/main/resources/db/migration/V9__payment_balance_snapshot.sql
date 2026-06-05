-- ===================================================================
-- V9: Snapshot the resulting balance/status onto each payment.
--   Previously the API derived a payment's status/balance from the
--   live bill, so once a bill was fully paid every history row showed
--   PAID / 0. Storing a per-payment snapshot keeps each row showing the
--   state it had when that payment was recorded.
--   Portable DDL: PostgreSQL (runtime) and H2/PostgreSQL mode (tests).
-- ===================================================================

ALTER TABLE payments ADD COLUMN balance_after NUMERIC(14,2);
ALTER TABLE payments ADD COLUMN status_after  VARCHAR(30);

-- Backfill: reconstruct each payment's resulting balance from the running
-- total of payments on its bill (ordered by id, which is monotonic with the
-- order payments were recorded). status_after is PAID once the balance hits
-- zero, otherwise PARTIALLY_PAID.
UPDATE payments p
SET balance_after = GREATEST(
        (SELECT b.total_amount FROM bills b WHERE b.id = p.bill_id)
        - (SELECT COALESCE(SUM(p2.amount), 0) FROM payments p2
           WHERE p2.bill_id = p.bill_id AND p2.id <= p.id),
        0);

UPDATE payments p
SET status_after = CASE
        WHEN (SELECT b.total_amount FROM bills b WHERE b.id = p.bill_id)
             - (SELECT COALESCE(SUM(p2.amount), 0) FROM payments p2
                WHERE p2.bill_id = p.bill_id AND p2.id <= p.id) <= 0
        THEN 'PAID' ELSE 'PARTIALLY_PAID' END;

ALTER TABLE payments ALTER COLUMN balance_after SET NOT NULL;
ALTER TABLE payments ALTER COLUMN status_after  SET NOT NULL;
