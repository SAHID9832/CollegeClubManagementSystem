package com.collegeclub.dao;

import com.collegeclub.model.Event;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventDao {
    Event save(Event event) throws SQLException;
    Optional<Event> findById(int id) throws SQLException;
    List<Event> findAll() throws SQLException;
    List<Event> findByClubId(int clubId) throws SQLException;
    boolean update(Event event) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countTotalEvents() throws SQLException;
    List<Event> getAllEvents();
}
