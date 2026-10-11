package com.collegeclub.model;

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

    public Club() {
        this.category = "General";
        this.capacity = 100;
        this.active = true;
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

    public int getId() {
        return id;
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
        this.description = (description != null) ? description.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.trim().isEmpty()) ? category.trim() : "General";
    }

    public String getCoordinator() {
        return coordinator;
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
        if (capacity < 1) {
            throw new IllegalArgumentException("Club capacity must be at least 1.");
        }
        this.capacity = capacity;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        if (memberCount < 0) {
            throw new IllegalArgumentException("Member count cannot be negative.");
        }
        this.memberCount = memberCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFull() {
        return capacity > 0 && memberCount >= capacity;
    }

    public void displayClub() {
        System.out.println("\n--- Club Details ---");
        System.out.println("ID         : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Category   : " + category);
        System.out.println("Coordinator: " + coordinator);
        System.out.println("Members    : " + memberCount + "/" + capacity);
    }
}
