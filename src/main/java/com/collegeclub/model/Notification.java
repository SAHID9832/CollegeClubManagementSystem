package com.collegeclub.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification Entity for in-app student and admin alerts.
 */
public class Notification {

    private int id;
    private int userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private String createdAt;

    public Notification() {
        this.type = "INFO";
        this.isRead = false;
        this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Notification(int id, int userId, String title, String message, String type, boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : "INFO";
        this.isRead = isRead;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Notification(int userId, String title, String message, String type) {
        this(0, userId, title, message, type, false, null);
    }

    public int getId() {
        return id;
    }

    public int getNotificationId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
