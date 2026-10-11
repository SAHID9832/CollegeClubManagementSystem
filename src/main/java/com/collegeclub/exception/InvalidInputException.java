package com.collegeclub.exception;

/**
 * Preserved faculty exception for invalid inputs.
 * Extends ClubSystemException while preserving original constructors and message style.
 */
public class InvalidInputException extends ClubSystemException {

    public InvalidInputException(String message) {
        super("INVALID_INPUT", message, 400);
    }

    public InvalidInputException(String errorCode, String message) {
        super(errorCode, message, 400);
    }
}
