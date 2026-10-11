package com.collegeclub.exception;

public class MembershipLimitReachedException extends ClubSystemException {
    public MembershipLimitReachedException(String message) {
        super("MEMBERSHIP_LIMIT_REACHED", message, 409);
    }
}
