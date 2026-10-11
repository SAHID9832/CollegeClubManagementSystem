package com.collegeclub.exception;

public class DuplicateMembershipException extends ClubSystemException {
    public DuplicateMembershipException(String message) {
        super("DUPLICATE_MEMBERSHIP", message, 409);
    }
}
