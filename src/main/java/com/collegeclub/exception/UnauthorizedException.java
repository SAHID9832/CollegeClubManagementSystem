package com.collegeclub.exception;

public class UnauthorizedException extends ClubSystemException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, 401);
    }
}
