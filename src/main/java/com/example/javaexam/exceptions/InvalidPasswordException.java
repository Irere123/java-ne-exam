package com.example.javaexam.exceptions;

/** Thrown when the supplied current password does not match (change password). */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
