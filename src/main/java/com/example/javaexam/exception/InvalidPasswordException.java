package com.example.javaexam.exception;

/** Thrown when the supplied current password does not match (change password). */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
