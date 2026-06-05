package com.example.javaexam.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.example.javaexam.utils.DateRules;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Asserts that a {@link java.time.LocalDate} is plausible for this system:
 * never before {@link DateRules#SYSTEM_EPOCH}, and not in the future unless
 * {@link #allowFuture()} is set — in which case it may be at most
 * {@link #maxFutureYears()} years ahead.
 *
 * <p>{@code null} is treated as valid so this composes with {@code @NotNull}:
 * use {@code @NotNull} to require the field and {@code @PlausibleDate} to bound
 * its range.
 */
@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = PlausibleDateValidator.class)
public @interface PlausibleDate {

    String message() default "must be a plausible date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /** Allow dates after today (e.g. a tariff scheduled to take effect later). */
    boolean allowFuture() default false;

    /** When {@link #allowFuture()} is true, the furthest date ahead accepted. */
    int maxFutureYears() default 1;
}
