package com.example.javaexam.exceptions;

/** Thrown when an email-verification token is missing, expired, or already used. */
public class VerificationException extends RuntimeException {
    public VerificationException(String message) {
        super(message);
    }
}
