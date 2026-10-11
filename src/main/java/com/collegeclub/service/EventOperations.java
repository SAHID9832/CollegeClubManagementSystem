package com.collegeclub.service;

import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateMembershipException;
import com.collegeclub.exception.EventFullException;
import com.collegeclub.exception.EventNotFoundException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.Event;
import com.collegeclub.model.User;

import java.util.List;

public interface EventOperations {
    Event createEvent(Event event) throws InvalidInputException, ClubNotFoundException, ClubSystemException;
    Event getEventById(int id) throws EventNotFoundException, ClubSystemException;
    List<Event> getAllEvents() throws ClubSystemException;
    List<Event> getEventsByClub(int clubId) throws ClubNotFoundException, ClubSystemException;
    boolean registerForEvent(int eventId, int userId) throws EventNotFoundException, DuplicateMembershipException, EventFullException, ClubSystemException;
    boolean cancelEventRegistration(int eventId, int userId) throws EventNotFoundException, ClubSystemException;
    boolean updateEvent(Event event) throws EventNotFoundException, InvalidInputException, ClubSystemException;
    boolean deleteEvent(int eventId) throws EventNotFoundException, ClubSystemException;
    List<Event> getUserEvents(int userId) throws ClubSystemException;
    List<User> getEventAttendees(int eventId) throws EventNotFoundException, ClubSystemException;
}
