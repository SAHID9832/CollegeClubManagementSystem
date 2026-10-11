package com.collegeclub.phase1;

/**
 * Faculty Reference Phase 1 Validation Logic.
 * Demonstrates basic string manipulation and character inspection in Java:
 * - trim()
 * - isEmpty()
 * - length()
 * - charAt() / toCharArray()
 * - Character.isDigit()
 */
public class Validation {

    public static void validateUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty.");
        }
        if (username.trim().length() < 5) {
            throw new Exception("Username should contain at least 5 characters.");
        }
    }

    public static void validatePassword(String password) throws Exception {
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Password cannot be empty.");
        }
        if (password.length() < 8) {
            throw new Exception("Password should contain at least 8 characters.");
        }
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            throw new Exception("Password must contain at least one digit.");
        }
    }
}
