package com.collegeclub.exception;

public class InvalidPasswordException extends InvalidInputException {
    public InvalidPasswordException(String message) {
        super("INVALID_PASSWORD", message);
    }
}
