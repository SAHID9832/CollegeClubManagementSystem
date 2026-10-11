package com.collegeclub.exception;

public class EventNotFoundException extends ClubSystemException {
    public EventNotFoundException(String message) {
        super("EVENT_NOT_FOUND", message, 404);
    }
}
