package com.collegeclub.exception;

public class InvalidEventRegistrationException extends InvalidInputException {
    public InvalidEventRegistrationException(String message) {
        super("INVALID_EVENT_REGISTRATION", message);
    }
}
