package com.collegeclub.exception;

public class InvalidRequestException extends ClubSystemException {
    public InvalidRequestException(String message) {
        super("INVALID_REQUEST", message, 400);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super("INVALID_REQUEST", message, 400, cause);
    }
}
