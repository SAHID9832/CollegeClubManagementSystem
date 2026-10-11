package com.collegeclub.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enterprise User Entity for the College Club Management System.
 * Demonstrates encapsulation, data hiding (password hash stored, never plain password),
 * and object-oriented modeling.
 */
public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private Role role;
    private String createdAt;

    public User() {
        this.role = Role.STUDENT;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public User(int id, String username, String passwordHash, String email, String fullName, Role role) {
        this();
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.role = (role != null) ? role : Role.STUDENT;
    }

    public User(String username, String passwordHash, String email, String fullName, Role role) {
        this(0, username, passwordHash, email, fullName, role);
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            this.username = username.trim();
        }
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.trim().toLowerCase();
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null) {
            this.fullName = fullName.trim();
        }
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = (role != null) ? role : Role.STUDENT;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Display safe user details.
     * Note: As required by security guidelines, passwords and hashes are strictly excluded.
     */
    public void displayUser() {
        System.out.println("\n--- User Profile ---");
        System.out.println("User ID   : " + id);
        System.out.println("Username  : " + username);
        System.out.println("Full Name : " + fullName);
        System.out.println("Email     : " + email);
        System.out.println("Role      : " + role);
        System.out.println("Created At: " + createdAt);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}
