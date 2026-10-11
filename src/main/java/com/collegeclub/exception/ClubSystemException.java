package com.collegeclub.exception;

/**
 * Base domain exception for the College Club Management System.
 * Carries a machine-readable error code and an HTTP status hint for API serialization.
 */
public class ClubSystemException extends Exception {

    private final String errorCode;
    private final int httpStatus;

    public ClubSystemException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ClubSystemException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
