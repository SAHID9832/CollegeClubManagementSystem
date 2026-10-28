package com.collegeclub.phase1;

/**
 * Phase 1 Registration Model.
 * Represents an event registration linking a student and an event with encapsulated attributes:
 * registrationId, username, eventTitle, registrationDate, and status.
 * Demonstrates default & parameterized constructors, getters/setters,
 * and the displayRegistration() method.
 */
public class Registration {

    private int registrationId;
    private String username;
    private String eventTitle;
    private String registrationDate;
    private String status;

    public Registration() {
        this.status = "CONFIRMED";
    }

    public Registration(int registrationId, String username, String eventTitle,
                        String registrationDate, String status) {
        this.registrationId = registrationId;
        this.username = username;
        this.eventTitle = eventTitle;
        this.registrationDate = registrationDate;
        this.status = (status != null && !status.trim().isEmpty()) ? status : "CONFIRMED";
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayRegistration() {
        System.out.println("\n--- Registration Details ---");
        System.out.println("Registration ID : " + registrationId);
        System.out.println("Student/User    : " + username);
        System.out.println("Event Registered: " + eventTitle);
        System.out.println("Date            : " + registrationDate);
        System.out.println("Status          : " + status);
    }
}
