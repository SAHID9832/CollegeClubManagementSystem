package com.collegeclub.exception;

public class InvalidUsernameException extends InvalidInputException {
    public InvalidUsernameException(String message) {
        super("INVALID_USERNAME", message);
    }
}
