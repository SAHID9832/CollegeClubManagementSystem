package com.collegeclub.dao;

import com.collegeclub.model.Event;
import com.collegeclub.model.User;
import java.sql.SQLException;
import java.util.List;

public interface EventRegistrationDao {
    boolean register(int eventId, int userId) throws SQLException;
    boolean cancel(int eventId, int userId) throws SQLException;
    boolean isRegistered(int eventId, int userId) throws SQLException;
    int countRegistrations(int eventId) throws SQLException;
    List<Event> findEventsByUserId(int userId) throws SQLException;
    List<User> findAttendeesByEventId(int eventId) throws SQLException;
    int countTotalRegistrations() throws SQLException;
}
