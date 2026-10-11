package com.collegeclub.dao.impl;

import com.collegeclub.dao.EventDao;
import com.collegeclub.model.Event;
import com.collegeclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventDaoImpl implements EventDao {

    @Override
    public Event save(Event event) throws SQLException {
        String sql = "INSERT INTO events (club_id, title, description, event_date, venue, capacity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, event.getClubId());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setString(4, event.getEventDate());
            ps.setString(5, event.getVenue());
            ps.setInt(6, event.getCapacity());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setId(rs.getInt(1));
                }
            }
            return event;
        }
    }

    @Override
    public Optional<Event> findById(int id) throws SQLException {
        String sql = "SELECT e.id, e.club_id, e.title, e.description, e.event_date, e.venue, e.capacity, " +
                "c.name AS club_name, " +
                "(SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count " +
                "FROM events e LEFT JOIN clubs c ON e.club_id = c.id WHERE e.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Event> findAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.id, e.club_id, e.title, e.description, e.event_date, e.venue, e.capacity, " +
                "c.name AS club_name, " +
                "(SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count " +
                "FROM events e LEFT JOIN clubs c ON e.club_id = c.id ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Event> findByClubId(int clubId) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.id, e.club_id, e.title, e.description, e.event_date, e.venue, e.capacity, " +
                "c.name AS club_name, " +
                "(SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count " +
                "FROM events e LEFT JOIN clubs c ON e.club_id = c.id WHERE e.club_id = ? ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean update(Event event) throws SQLException {
        String sql = "UPDATE events SET club_id = ?, title = ?, description = ?, event_date = ?, venue = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, event.getClubId());
            ps.setString(2, event.getTitle());
            ps.setString(3, event.getDescription());
            ps.setString(4, event.getEventDate());
            ps.setString(5, event.getVenue());
            ps.setInt(6, event.getCapacity());
            ps.setInt(7, event.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int countTotalEvents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM events";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setClubId(rs.getInt("club_id"));
        e.setClubName(rs.getString("club_name"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setEventDate(rs.getString("event_date"));
        e.setVenue(rs.getString("venue"));
        e.setCapacity(rs.getInt("capacity"));
        e.setRegisteredCount(rs.getInt("registered_count"));
        return e;
    }
}
