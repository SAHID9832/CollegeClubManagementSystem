package com.collegeclub.model;

/**
 * Event Entity representing club activities, competitions, and workshops.
 * Demonstrates encapsulation with input validation rules.
 */
public class Event {

    private int id;
    private int clubId;
    private String clubName;
    private String title;
    private String description;
    private String eventDate;
    private String venue;
    private int capacity;
    private int registeredCount;

    public Event() {
        this.capacity = 50;
    }

    public Event(int id, int clubId, String title, String description, String eventDate, String venue, int capacity) {
        this();
        setId(id);
        setClubId(clubId);
        setTitle(title);
        setDescription(description);
        setEventDate(eventDate);
        setVenue(venue);
        setCapacity(capacity);
    }

    public Event(int clubId, String title, String description, String eventDate, String venue, int capacity) {
        this(0, clubId, title, description, eventDate, venue, capacity);
    }

    public Event(int id, String title, String eventDate, String venue) {
        this(id, 1, title, "", eventDate, venue, 50);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Event ID cannot be negative.");
        }
        this.id = id;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        if (clubId < 0) {
            throw new IllegalArgumentException("Club ID cannot be negative.");
        }
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().length() < 3) {
            throw new IllegalArgumentException("Event title must contain at least 3 characters.");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        if (eventDate == null || eventDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Event date cannot be empty.");
        }
        this.eventDate = eventDate.trim();
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        if (venue == null || venue.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue cannot be empty.");
        }
        this.venue = venue.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Event capacity must be at least 1.");
        }
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        if (registeredCount < 0) {
            throw new IllegalArgumentException("Registered count cannot be negative.");
        }
        this.registeredCount = registeredCount;
    }

    public int getSeatsLeft() {
        return Math.max(0, capacity - registeredCount);
    }

    public boolean isFull() {
        return registeredCount >= capacity;
    }

    public void displayEvent() {
        System.out.println("\n--- Event Details ---");
        System.out.println("ID        : " + id);
        System.out.println("Title     : " + title);
        System.out.println("Date      : " + eventDate);
        System.out.println("Venue     : " + venue);
        System.out.println("Capacity  : " + registeredCount + "/" + capacity);
    }
}
