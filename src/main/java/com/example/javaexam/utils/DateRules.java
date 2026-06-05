package com.example.javaexam.utils;

import java.time.LocalDate;

/** System-wide date boundaries shared by the date validators and the services. */
public final class DateRules {

    /**
     * The earliest date any business event may carry. Nothing in this system —
     * a meter, reading, payment or tariff — legitimately predates this, so it
     * guards against typos such as a year of {@code 1900} or {@code 0202}.
     */
    public static final LocalDate SYSTEM_EPOCH = LocalDate.of(2000, 1, 1);

    /** A tariff may be scheduled to take effect at most this many years ahead. */
    public static final int MAX_TARIFF_LEAD_YEARS = 5;

    private DateRules() {
    }
}
