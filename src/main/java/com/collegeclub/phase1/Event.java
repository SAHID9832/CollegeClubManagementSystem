package com.collegeclub.phase1;

/**
 * Phase 1 Event Model.
 * Represents an event organized by a college club with encapsulated attributes:
 * id, title, clubName, category, eventDate, venue, capacity, and registeredCount.
 * Demonstrates default & parameterized constructors, getters/setters,
 * and the displayEvent() method.
 */
public class Event {

    private int id;
    private String title;
    private String clubName;
    private String category;
    private String eventDate;
    private String venue;
    private int capacity;
    private int registeredCount;

    public Event() {
        this.capacity = 50;
        this.registeredCount = 0;
        this.category = "Technical";
    }

    public Event(int id, String title, String clubName, String category,
                 String eventDate, String venue, int capacity) {
        this.id = id;
        this.title = title;
        this.clubName = clubName;
        this.category = category;
        this.eventDate = eventDate;
        this.venue = venue;
        this.capacity = capacity;
        this.registeredCount = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public void displayEvent() {
        System.out.println("\n--- Event Details ---");
        System.out.println("Event ID    : " + id);
        System.out.println("Title       : " + title);
        System.out.println("Club        : " + clubName);
        System.out.println("Category    : " + category);
        System.out.println("Date & Time : " + eventDate);
        System.out.println("Venue       : " + venue);
        System.out.println("Capacity    : " + registeredCount + "/" + capacity);
    }
}
