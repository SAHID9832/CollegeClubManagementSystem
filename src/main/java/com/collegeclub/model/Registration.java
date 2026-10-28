package com.collegeclub.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enterprise Registration Model.
 * Represents an event registration linking a student and an event with full encapsulation.
 */
public class Registration {

    private int id;
    private int eventId;
    private int userId;
    private String studentName;
    private String username;
    private String email;
    private String eventTitle;
    private String clubName;
    private String studentId;
    private String department;
    private String course;
    private String status;
    private String registeredAt;

    public Registration() {
        this.status = "CONFIRMED";
        this.registeredAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Registration(int id, int eventId, int userId, String studentName, String eventTitle, String status, String registeredAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.studentName = studentName;
        this.eventTitle = eventTitle;
        this.status = (status != null && !status.trim().isEmpty()) ? status : "CONFIRMED";
        this.registeredAt = (registeredAt != null) ? registeredAt : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Registration(int id, int eventId, int userId, String status) {
        this(id, eventId, userId, null, null, status, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public void displayRegistration() {
        System.out.println("\n--- Event Registration ---");
        System.out.println("Registration ID : " + id);
        System.out.println("Event ID        : " + eventId + (eventTitle != null ? " (" + eventTitle + ")" : ""));
        System.out.println("User ID         : " + userId + (studentName != null ? " (" + studentName + ")" : ""));
        System.out.println("Status          : " + status);
        System.out.println("Registered At   : " + registeredAt);
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", registeredAt='" + registeredAt + '\'' +
                '}';
    }
}
