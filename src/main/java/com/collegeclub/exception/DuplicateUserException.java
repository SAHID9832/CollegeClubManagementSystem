package com.collegeclub.exception;

public class DuplicateUserException extends ClubSystemException {
    public DuplicateUserException(String message) {
        super("DUPLICATE_USER", message, 409);
    }
}
