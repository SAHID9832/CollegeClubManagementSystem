package com.collegeclub.exception;

public class AuthenticationException extends ClubSystemException {
    public AuthenticationException(String message) {
        super("AUTHENTICATION_FAILED", message, 401);
    }
}
