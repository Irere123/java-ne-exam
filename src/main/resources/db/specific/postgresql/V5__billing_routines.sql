-- ===================================================================
-- V5 (PostgreSQL): Database routines and messaging (Task 6)
--
--   * TRIGGER on INSERT into bills  -> inserts a BILL_GENERATED notification.
--   * TRIGGER on UPDATE of bills     -> when a bill becomes fully PAID, inserts
--                                       a PAYMENT_COMPLETED notification.
--   * STORED PROCEDURE + CURSOR      -> sp_apply_late_penalties() walks overdue,
--                                       unpaid bills and applies the tariff's
--                                       late-payment penalty.
--
-- Notification message format (per the exam):
--   Dear <CustomerName>,
--   Your <Month/Year> utility bill of <Amount> FRW has been successfully processed.
-- ===================================================================

-- --- Trigger function: notify on bill generation -----------------------------
CREATE OR REPLACE FUNCTION fn_notify_bill_generated()
    RETURNS TRIGGER AS
$$
DECLARE
    v_customer_name customers.full_name%TYPE;
BEGIN
    SELECT full_name INTO v_customer_name FROM customers WHERE id = NEW.customer_id;

    INSERT INTO notifications (customer_id, bill_id, type, message, created_at)
    VALUES (
        NEW.customer_id,
        NEW.id,
        'BILL_GENERATED',
        'Dear ' || COALESCE(v_customer_name, 'Customer') || ',' || chr(10) ||
        'Your ' || LPAD(NEW.billing_month::text, 2, '0') || '/' || NEW.billing_year::text ||
        ' utility bill of ' || TO_CHAR(NEW.total_amount, 'FM999999990.00') ||
        ' FRW has been successfully processed.',
        CURRENT_TIMESTAMP
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bill_after_insert
    AFTER INSERT ON bills
    FOR EACH ROW
EXECUTE FUNCTION fn_notify_bill_generated();

-- --- Trigger function: notify on full payment --------------------------------
CREATE OR REPLACE FUNCTION fn_notify_bill_paid()
    RETURNS TRIGGER AS
$$
DECLARE
    v_customer_name customers.full_name%TYPE;
BEGIN
    IF NEW.status = 'PAID' AND OLD.status <> 'PAID' THEN
        SELECT full_name INTO v_customer_name FROM customers WHERE id = NEW.customer_id;

        INSERT INTO notifications (customer_id, bill_id, type, message, created_at)
        VALUES (
            NEW.customer_id,
            NEW.id,
            'PAYMENT_COMPLETED',
            'Dear ' || COALESCE(v_customer_name, 'Customer') || ',' || chr(10) ||
            'Your ' || LPAD(NEW.billing_month::text, 2, '0') || '/' || NEW.billing_year::text ||
            ' utility bill of ' || TO_CHAR(NEW.total_amount, 'FM999999990.00') ||
            ' FRW has been fully paid. Thank you.',
            CURRENT_TIMESTAMP
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bill_after_update
    AFTER UPDATE ON bills
    FOR EACH ROW
EXECUTE FUNCTION fn_notify_bill_paid();

-- --- Stored procedure with an explicit CURSOR: apply late penalties ----------
-- Walks every approved/partially-paid/overdue bill that is past its due date and
-- still carries a balance, and adds the tariff's penalty_rate as a one-off
-- penalty. penalty_applied guards against charging the same bill twice.
CREATE OR REPLACE PROCEDURE sp_apply_late_penalties()
    LANGUAGE plpgsql AS
$$
DECLARE
    bill_cur CURSOR FOR
        SELECT b.id AS bill_id, b.outstanding_balance, t.penalty_rate
        FROM bills b
                 JOIN tariffs t ON t.id = b.tariff_id
        WHERE b.status IN ('APPROVED', 'PARTIALLY_PAID', 'OVERDUE')
          AND b.outstanding_balance > 0
          AND b.due_date < CURRENT_DATE
          AND b.penalty_applied = FALSE;
    rec       RECORD;
    v_penalty NUMERIC(14, 2);
BEGIN
    OPEN bill_cur;
    LOOP
        FETCH bill_cur INTO rec;
        EXIT WHEN NOT FOUND;

        v_penalty := ROUND(rec.outstanding_balance * rec.penalty_rate / 100, 2);

        UPDATE bills
        SET penalty_amount      = penalty_amount + v_penalty,
            total_amount        = total_amount + v_penalty,
            outstanding_balance = outstanding_balance + v_penalty,
            penalty_applied     = TRUE,
            status              = 'OVERDUE',
            updated_at          = CURRENT_TIMESTAMP
        WHERE id = rec.bill_id;
    END LOOP;
    CLOSE bill_cur;
END;
$$;
