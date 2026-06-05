package com.example.javaexam.models.enums;

/**
 * Application roles. Stored in the database as a string (see {@code users.role}).
 * Spring Security authorities are derived as {@code "ROLE_" + name()}
 * (e.g. {@code ROLE_ADMIN}, {@code ROLE_CUSTOMER}).
 *
 * <ul>
 *   <li>{@code ADMIN} — configure tariffs, approve bills, manage users.</li>
 *   <li>{@code OPERATOR} — capture meter readings.</li>
 *   <li>{@code FINANCE} — approve bills and payments.</li>
 *   <li>{@code CUSTOMER} — view own bills and payment history (default for self-signup).</li>
 * </ul>
 */
public enum Role {
    ADMIN,
    OPERATOR,
    FINANCE,
    CUSTOMER
}
