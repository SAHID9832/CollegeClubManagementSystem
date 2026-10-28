package com.collegeclub.phase1;

/**
 * Faculty Reference Phase 1 User Model (Module 1).
 * Represents the fundamental user entity with encapsulated username and password fields.
 * Demonstrates default & parameterized constructors, getters/setters,
 * and the displayUser() method matching the faculty lab manual.
 */
public class User {

    private String username;
    private String password;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter Methods
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setter Methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Display User Details
    public void displayUser() {
        System.out.println("\nUser Details");
        System.out.println("Username : " + username);
        System.out.println("Password : " + password);
    }
}
