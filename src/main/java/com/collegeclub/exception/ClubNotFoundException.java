package com.collegeclub.exception;

public class ClubNotFoundException extends ClubSystemException {
    public ClubNotFoundException(String message) {
        super("CLUB_NOT_FOUND", message, 404);
    }
}
