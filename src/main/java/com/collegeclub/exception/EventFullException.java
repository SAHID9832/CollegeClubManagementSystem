package com.collegeclub.exception;

public class EventFullException extends ClubSystemException {
    public EventFullException(String message) {
        super("EVENT_FULL", message, 409);
    }
}
