package com.collegeclub.exception;

public class ForbiddenException extends ClubSystemException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message, 403);
    }
}
