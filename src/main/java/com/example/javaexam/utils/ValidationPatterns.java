package com.example.javaexam.utils;

public final class ValidationPatterns {

    public static final String HUMAN_NAME = "^(?=.*\\p{L})[\\p{L}][\\p{L} '-]{1,99}$";
    public static final String STRONG_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s])\\S{8,72}$";
    public static final String UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
    public static final String JWT = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";

    /** Phone number: optional leading {@code +} and 10-15 digits (e.g. +250788123456). */
    public static final String PHONE = "^\\+?[0-9]{10,15}$";

    /** Country dialing code: a leading {@code +} and 1-4 digits (e.g. {@code +250} for Rwanda). */
    public static final String COUNTRY_CODE = "^\\+[0-9]{1,4}$";

    /** Subscriber number without the country code: 6-12 digits (e.g. {@code 788123456}). */
    public static final String PHONE_NATIONAL = "^[0-9]{6,12}$";

    /** Rwandan National ID: exactly 16 digits. */
    public static final String NATIONAL_ID = "^[0-9]{16}$";

    /** Meter number: 4-20 uppercase letters, digits or hyphens (e.g. WTR-000123). */
    public static final String METER_NUMBER = "^[A-Z0-9-]{4,20}$";

    private ValidationPatterns() {
    }
}
