package com.example.javaexam.validation;

import com.example.javaexam.utils.DateRules;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/** Enforces the {@link PlausibleDate} bounds against today's date. */
public class PlausibleDateValidator implements ConstraintValidator<PlausibleDate, LocalDate> {

    private boolean allowFuture;
    private int maxFutureYears;

    @Override
    public void initialize(PlausibleDate annotation) {
        this.allowFuture = annotation.allowFuture();
        this.maxFutureYears = annotation.maxFutureYears();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // presence is governed by @NotNull
        }
        // Too-far-in-the-past gets its own message; the annotation's (field-specific)
        // message covers the "not in the future" / lead-time case below.
        if (value.isBefore(DateRules.SYSTEM_EPOCH)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "must be a real date no earlier than " + DateRules.SYSTEM_EPOCH)
                    .addConstraintViolation();
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate latestAllowed = allowFuture ? today.plusYears(maxFutureYears) : today;
        return !value.isAfter(latestAllowed);
    }
}
