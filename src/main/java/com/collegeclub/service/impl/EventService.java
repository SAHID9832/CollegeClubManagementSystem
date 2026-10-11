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
}
