package com.ey.filter.csrf.exception;


public class MissingCsrfTokenException extends RuntimeException {

    private static final long serialVersionUID = -8729008988426362269L;

    public MissingCsrfTokenException(String actualToken) {
        super("Could not verify the provided CSRF token because your session was not found.");
    }
}
