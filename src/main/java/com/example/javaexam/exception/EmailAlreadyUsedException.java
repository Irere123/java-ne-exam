package com.example.javaexam.exception;

/** Thrown when registration is attempted with an email that already exists. */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("An account with email '" + email + "' already exists");
    }
}
