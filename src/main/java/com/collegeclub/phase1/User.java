package com.collegeclub.phase1;

/**
 * Faculty Reference Phase 1 User Model.
 * Represents the fundamental user entity with encapsulated username and password fields.
 * Demonstrates default & parameterized constructors, encapsulation, and displayUser method.
 */
public class User {

    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void displayUser() {
        System.out.println("\n--- User Details ---");
        System.out.println("Username : " + username);
    }
}
