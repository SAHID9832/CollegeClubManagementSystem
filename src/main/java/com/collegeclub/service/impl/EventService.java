package com.collegeclub.service.impl;

import com.collegeclub.dao.ClubDao;
import com.collegeclub.dao.EventDao;
import com.collegeclub.dao.EventRegistrationDao;
import com.collegeclub.dao.impl.ClubDaoImpl;
import com.collegeclub.dao.impl.EventDaoImpl;
import com.collegeclub.dao.impl.EventRegistrationDaoImpl;
import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateMembershipException;
import com.collegeclub.exception.EventFullException;
import com.collegeclub.exception.EventNotFoundException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.User;
import com.collegeclub.service.EventOperations;

import java.sql.SQLException;
import java.util.List;

public class EventService implements EventOperations {

    private final EventDao eventDao;
    private final EventRegistrationDao eventRegistrationDao;
    private final ClubDao clubDao;

    public EventService() {
        this(new EventDaoImpl(), new EventRegistrationDaoImpl(), new ClubDaoImpl());
    }

    public EventService(EventDao eventDao, EventRegistrationDao eventRegistrationDao, ClubDao clubDao) {
        this.eventDao = eventDao;
        this.eventRegistrationDao = eventRegistrationDao;
        this.clubDao = clubDao;
        seedDefaultEvents();
    }

    @Override
    public Event createEvent(Event event) throws InvalidInputException, ClubNotFoundException, ClubSystemException {
        if (event == null) {
            throw new InvalidInputException("Event data cannot be null.");
        }
        if (event.getTitle() == null || event.getTitle().trim().length() < 3) {
            throw new InvalidInputException("Event title must contain at least 3 characters.");
        }
        if (event.getEventDate() == null || event.getEventDate().trim().isEmpty()) {
            throw new InvalidInputException("Event date is required.");
        }
        if (event.getVenue() == null || event.getVenue().trim().isEmpty()) {
            throw new InvalidInputException("Event venue is required.");
        }

        try {
            if (event.getClubId() > 0 && clubDao.findById(event.getClubId()).isEmpty()) {
                throw new ClubNotFoundException("Associated club ID " + event.getClubId() + " does not exist.");
            }
            return eventDao.save(event);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error saving event: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public Event getEventById(int id) throws EventNotFoundException, ClubSystemException {
        try {
            return eventDao.findById(id)
                    .orElseThrow(() -> new EventNotFoundException("Event with ID " + id + " not found."));
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving event: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Event> getAllEvents() throws ClubSystemException {
        try {
            return eventDao.findAll();
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving events: " + e.getMessage(), 500, e);
        }
    }

    public List<Event> getUpcomingEvents() throws ClubSystemException {
        return getAllEvents();
    }

    public List<Event> getStudentEvents(int userId) throws ClubSystemException {
        return getUserEvents(userId);
    }

    public Event scheduleEvent(int clubId, String title, String description, java.time.LocalDateTime eventDate, String venue, int capacity)
            throws ClubSystemException, InvalidInputException {
        String dtStr = eventDate != null ? eventDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "2026-11-01 10:00";
        Event ev = new Event(clubId, title, description, dtStr, venue, capacity);
        return createEvent(ev);
    }

    @Override
    public List<Event> getEventsByClub(int clubId) throws ClubNotFoundException, ClubSystemException {
        try {
            if (clubDao.findById(clubId).isEmpty()) {
                throw new ClubNotFoundException("Club ID " + clubId + " not found.");
            }
            return eventDao.findByClubId(clubId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving club events: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean registerForEvent(int eventId, int userId) throws EventNotFoundException, DuplicateMembershipException, EventFullException, ClubSystemException {
        Event event = getEventById(eventId);

        try {
            if (eventRegistrationDao.isRegistered(eventId, userId)) {
                throw new DuplicateMembershipException("You are already registered for this event.");
            }

            int currentRegistrations = eventRegistrationDao.countRegistrations(eventId);
            if (event.getCapacity() > 0 && currentRegistrations >= event.getCapacity()) {
                throw new EventFullException("Event has reached maximum registration capacity (" + event.getCapacity() + " seats).");
            }

            return eventRegistrationDao.register(eventId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error registering for event: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean cancelEventRegistration(int eventId, int userId) throws EventNotFoundException, ClubSystemException {
        getEventById(eventId); // verifies existence

        try {
            if (!eventRegistrationDao.isRegistered(eventId, userId)) {
                throw new ClubSystemException("NOT_REGISTERED", "User is not registered for this event.", 400);
            }
            return eventRegistrationDao.cancel(eventId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error cancelling registration: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean updateEvent(Event event) throws EventNotFoundException, InvalidInputException, ClubSystemException {
        if (event == null || event.getId() <= 0) {
            throw new InvalidInputException("Valid Event ID is required for update.");
        }
        getEventById(event.getId());

        try {
            return eventDao.update(event);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error updating event: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean deleteEvent(int eventId) throws EventNotFoundException, ClubSystemException {
        getEventById(eventId);
        try {
            return eventDao.delete(eventId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error deleting event: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Event> getUserEvents(int userId) throws ClubSystemException {
        try {
            return eventRegistrationDao.findEventsByUserId(userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving user events: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<User> getEventAttendees(int eventId) throws EventNotFoundException, ClubSystemException {
        getEventById(eventId);
        try {
            return eventRegistrationDao.findAttendeesByEventId(eventId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving attendees: " + e.getMessage(), 500, e);
        }
    }

    private void seedDefaultEvents() {
        try {
            List<Club> clubs = clubDao.findAll();
            if (clubs.isEmpty()) return;

            int codingClubId = findClubIdByName(clubs, "ByteCraft", clubs.get(0).getId());
            int aiClubId = findClubIdByName(clubs, "RoboTech", codingClubId);
            int cyberClubId = findClubIdByName(clubs, "NullSector", codingClubId);
            int litClubId = findClubIdByName(clubs, "Athena", codingClubId);
            int photoClubId = findClubIdByName(clubs, "ShutterBug", codingClubId);
            int sportsClubId = findClubIdByName(clubs, "Titans", codingClubId);
            int musicClubId = findClubIdByName(clubs, "Rhapsody", codingClubId);
            int ecellId = findClubIdByName(clubs, "VentureLab", codingClubId);

            List<Event> existing = eventDao.findAll();
            java.util.Set<String> titles = new java.util.HashSet<>();
            for (Event e : existing) {
                if (e.getTitle() != null) {
                    titles.add(e.getTitle().trim().toLowerCase());
                }
            }

            Event[] defaults = {
                new Event(codingClubId, "Annual 24-Hour University Hackathon 2026",
                        "Build cutting-edge full-stack and AI software solutions. Cash prizes, mentor support, and industry judging.",
                        "2026-10-25 09:00", "Main Auditorium & Computing Center", 150),
                new Event(aiClubId, "Autonomous Robotics & AI Expo 2026",
                        "Live demonstrations of computer vision robots, obstacle-avoiding rovers, and generative AI models.",
                        "2026-10-28 10:30", "Robotics Innovation Lab - Block C", 100),
                new Event(cyberClubId, "Campus Cyber Defense CTF Challenge",
                        "Inter-collegiate ethical hacking challenge covering cryptography, reverse engineering, and web exploits.",
                        "2026-11-05 14:00", "Cyber Security Cyber-Range Lab", 80),
                new Event(litClubId, "National Parliamentary Debate Championship",
                        "Oxford-style debating on global affairs, AI ethics, and economic policy.",
                        "2026-11-12 10:00", "Senate Hall, Admin Building", 120),
                new Event(photoClubId, "Campus Life Photo Walk & Film Screening",
                        "Guided photography walk around heritage campus followed by screening of student short films.",
                        "2026-11-18 15:30", "Open Air Theatre & Media Studio", 90),
                new Event(sportsClubId, "Annual Inter-Department Athletics Meet",
                        "Sprint races, relay, long jump, badminton, and basketball tournaments across all university branches.",
                        "2026-11-25 08:00", "University Sports Complex Stadium", 200),
                new Event(musicClubId, "Acoustic Unplugged & Rock Band Night",
                        "Live instrumental, classical fusion, and acoustic band performances under the stars.",
                        "2026-12-02 18:00", "Campus Amphitheatre", 250),
                new Event(ecellId, "E-Summit 2026: Venture Pitch Showcase",
                        "Student founders pitch startup ideas to angel investors and venture capitalists with seed grants.",
                        "2026-12-10 11:00", "Convention Center Hall A", 120)
            };

            for (Event ev : defaults) {
                if (!titles.contains(ev.getTitle().trim().toLowerCase())) {
                    eventDao.save(ev);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice on seeding events: " + e.getMessage());
        }
    }

    private int findClubIdByName(List<Club> clubs, String name, int defaultId) {
        for (Club c : clubs) {
            if (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())) {
                return c.getId();
            }
        }
        return defaultId;
    }
}
