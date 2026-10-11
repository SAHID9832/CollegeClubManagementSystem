package com.collegeclub.util;

import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.exception.InvalidPasswordException;
import com.collegeclub.exception.InvalidUsernameException;

/**
 * Server-side Validation Engine.
 * Enforces the faculty's baseline rules as the absolute minimum while supporting enterprise checks.
 */
public class Validation {

    public static void validateUsername(String username) throws InvalidUsernameException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUsernameException("Username cannot be empty.");
        }
        String trimmed = username.trim();
        if (trimmed.length() < 5) {
            throw new InvalidUsernameException("Username should contain at least 5 characters.");
        }
        if (trimmed.length() > 50) {
            throw new InvalidUsernameException("Username must not exceed 50 characters.");
        }
    }

    public static void validatePassword(String password) throws InvalidPasswordException {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidPasswordException("Password cannot be empty.");
        }
        if (password.length() < 8) {
            throw new InvalidPasswordException("Password should contain at least 8 characters.");
        }
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            throw new InvalidPasswordException("Password must contain at least one digit.");
        }
    }

    public static void validateEmail(String email) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }
        String trimmed = email.trim();
        if (!trimmed.contains("@") || !trimmed.contains(".") || trimmed.indexOf('@') >= trimmed.lastIndexOf('.')) {
            throw new InvalidInputException("Invalid email address format.");
        }
    }

    public static void validateFullName(String fullName) throws InvalidInputException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidInputException("Full name cannot be empty.");
        }
        if (fullName.trim().length() < 2) {
            throw new InvalidInputException("Full name must contain at least 2 characters.");
        }
    }

    public static void validatePositiveId(int id, String entityName) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException(entityName + " ID must be greater than zero.");
        }
    }
}
