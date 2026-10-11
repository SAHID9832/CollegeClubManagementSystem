package com.collegeclub.model;

/**
 * System roles for Role-Based Access Control (RBAC).
 */
public enum Role {
    STUDENT,
    ADMIN,
    COORDINATOR;

    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return STUDENT;
        }
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
