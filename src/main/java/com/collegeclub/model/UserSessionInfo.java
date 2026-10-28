package com.collegeclub.model;

/**
 * Model representing an active or recent user session.
 * Used for session tracking, teacher inspection, and administrative security monitoring.
 */
public class UserSessionInfo {

    private String sessionId;
    private int userId;
    private String username;
    private String fullName;
    private String role;
    private String loginTime;
    private String lastAccessTime;
    private String ipAddress;
    private boolean active;

    public UserSessionInfo() {
        this.active = true;
    }

    public UserSessionInfo(String sessionId, int userId, String username, String fullName,
                           String role, String loginTime, String lastAccessTime,
                           String ipAddress, boolean active) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.loginTime = loginTime;
        this.lastAccessTime = lastAccessTime;
        this.ipAddress = ipAddress;
        this.active = active;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
