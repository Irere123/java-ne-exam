package com.example.javaexam.exception;

/** Thrown when a refresh token is invalid, expired, reused, or out of date. */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
