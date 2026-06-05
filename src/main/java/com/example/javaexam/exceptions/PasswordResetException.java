package com.example.javaexam.exceptions;

/** Thrown when a password-reset token is missing, expired, or already used. */
public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }
}
