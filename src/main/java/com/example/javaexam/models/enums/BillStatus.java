package com.example.javaexam.models.enums;

/**
 * Lifecycle of a postpaid bill.
 *
 * <ul>
 *   <li>{@code PENDING} — generated from a meter reading, awaiting approval.</li>
 *   <li>{@code APPROVED} — approved by finance/admin and payable.</li>
 *   <li>{@code PARTIALLY_PAID} — at least one payment recorded, balance remaining.</li>
 *   <li>{@code PAID} — outstanding balance reached zero.</li>
 *   <li>{@code OVERDUE} — past due date with a balance; a late penalty was applied.</li>
 * </ul>
 */
public enum BillStatus {
    PENDING,
    APPROVED,
    PARTIALLY_PAID,
    PAID,
    OVERDUE
}
