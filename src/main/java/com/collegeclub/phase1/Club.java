package com.collegeclub.phase1;

/**
 * Phase 1 Club Model.
 * Represents a student club/organization with encapsulated attributes:
 * id, name, category, coordinator, and capacity.
 * Demonstrates default & parameterized constructors, getters/setters,
 * and the displayClub() method.
 */
public class Club {

    private int id;
    private String name;
    private String category;
    private String coordinator;
    private int capacity;
    private int memberCount;

    public Club() {
        this.category = "General";
        this.capacity = 100;
        this.memberCount = 0;
    }

    public Club(int id, String name, String category, String coordinator, int capacity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.coordinator = coordinator;
        this.capacity = capacity;
        this.memberCount = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public void displayClub() {
        System.out.println("\n--- Club Details ---");
        System.out.println("Club ID     : " + id);
        System.out.println("Club Name   : " + name);
        System.out.println("Category    : " + category);
        System.out.println("Coordinator : " + coordinator);
        System.out.println("Capacity    : " + capacity);
        System.out.println("Members     : " + memberCount + "/" + capacity);
    }
}
