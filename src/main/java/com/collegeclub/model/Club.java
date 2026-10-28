package com.collegeclub.model;

import java.time.LocalDate;

/**
 * Club Entity representing a student organization.
 * Demonstrates strong encapsulation with validation in constructors and setters.
 */
public class Club {

    private int id;
    private String name;
    private String description;
    private String category;
    private String coordinator;
    private int capacity;
    private int memberCount;
    private boolean active;
    private String objectives;
    private String activities;
    private String icon;
    private String createdAt;

    public Club() {
        this.category = "General";
        this.capacity = 100;
        this.active = true;
        this.icon = "🛡️";
        this.createdAt = LocalDate.now().toString();
    }

    public Club(int id, String name, String description, String category, String coordinator, int capacity) {
        this();
        setId(id);
        setName(name);
        setDescription(description);
        setCategory(category);
        setCoordinator(coordinator);
        setCapacity(capacity);
    }

    public Club(String name, String description, String category, String coordinator, int capacity) {
        this(0, name, description, category, coordinator, capacity);
    }

    public Club(int id, String name, String description, String coordinator) {
        this(id, name, description, "General", coordinator, 100);
    }

    public Club(int id, String name, String description, String category, String coordinator,
                int capacity, String objectives, String activities, String icon) {
        this(id, name, description, category, coordinator, capacity);
        this.objectives = objectives;
        this.activities = activities;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public int getClubId() {
        return id;
    }

    public void setClubId(int clubId) {
        setId(clubId);
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Club ID cannot be negative.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getClubName() {
        return name;
    }

    public void setClubName(String clubName) {
        setName(clubName);
    }

    public void setName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("Club name must contain at least 3 characters.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryName() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category.trim() : "General";
    }

    public String getCoordinator() {
        return coordinator;
    }

    public String getCoordinatorName() {
        return coordinator;
    }

    public void setCoordinatorName(String coordinatorName) {
        setCoordinator(coordinatorName);
    }

    public String getCoordinatorEmail() {
        if (coordinator == null || coordinator.trim().isEmpty()) {
            return "coordinator@college.edu";
        }
        String cleaned = coordinator.toLowerCase()
                .replace("dr.", "")
                .replace("prof.", "")
                .trim()
                .replaceAll("[^a-z0-9]+", ".");
        if (cleaned.startsWith(".")) cleaned = cleaned.substring(1);
        if (cleaned.endsWith(".")) cleaned = cleaned.substring(0, cleaned.length() - 1);
        return (cleaned.isEmpty() ? "coordinator" : cleaned) + "@college.edu";
    }

    public void setCoordinator(String coordinator) {
        if (coordinator == null || coordinator.trim().isEmpty()) {
            throw new IllegalArgumentException("Club coordinator cannot be empty.");
        }
        this.coordinator = coordinator.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.capacity = capacity;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = Math.max(0, memberCount);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getObjectives() {
        return objectives != null ? objectives : "";
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives != null ? objectives.trim() : "";
    }

    public String getActivities() {
        return activities != null ? activities : "";
    }

    public void setActivities(String activities) {
        this.activities = activities != null ? activities.trim() : "";
    }

    public String getIcon() {
        return icon != null ? icon : "🛡️";
    }

    public String getCategoryIcon() {
        return getIcon();
    }

    public void setIcon(String icon) {
        this.icon = icon != null ? icon.trim() : "🛡️";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFull() {
        return memberCount >= capacity;
    }

    public int getAvailableSlots() {
        return Math.max(0, capacity - memberCount);
    }

    public void displayClub() {
        System.out.println("\n--- Club Details ---");
        System.out.println("ID          : " + id);
        System.out.println("Name        : " + name);
        System.out.println("Category    : " + category);
        System.out.println("Coordinator : " + coordinator);
        System.out.println("Capacity    : " + capacity);
        System.out.println("Members     : " + memberCount + "/" + capacity);
        System.out.println("Status      : " + (active ? "Active" : "Inactive"));
    }
}
