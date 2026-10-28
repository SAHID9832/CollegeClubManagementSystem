package com.collegeclub.phase1;

/**
 * Faculty Reference Phase 1 Validation Logic (Module 2).
 * Demonstrates basic string manipulation and character inspection in Java:
 * - trim()
 * - isEmpty()
 * - length()
 * - charAt() / toCharArray()
 * - Character.isDigit()
 * 
 * Preserves the exact faculty method signatures, logic, and console messages,
 * while throwing standard Exceptions on validation failure for robust error handling.
 */
public class Validation {

    // Username Validation
    public static boolean validateUsername(String username) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty.");
        }
        if (username.trim().length() < 5) {
            throw new Exception("Username should contain at least 5 characters.");
        }
        return true;
    }

    // Password Validation
    public static boolean validatePassword(String password) throws Exception {
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
        return true;
    }

    // Club Validation
    public static boolean validateClub(String name, int capacity) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Club name cannot be empty.");
        }
        if (name.trim().length() < 3) {
            throw new Exception("Club name should contain at least 3 characters.");
        }
        return validateCapacity(capacity);
    }

    // Event Validation
    public static boolean validateEvent(String title, int capacity) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new Exception("Event title cannot be empty.");
        }
        if (title.trim().length() < 3) {
            throw new Exception("Event title should contain at least 3 characters.");
        }
        return validateCapacity(capacity);
    }

    // Capacity Validation
    public static boolean validateCapacity(int capacity) throws Exception {
        if (capacity <= 0) {
            throw new Exception("Capacity must be greater than 0.");
        }
        return true;
    }
}
