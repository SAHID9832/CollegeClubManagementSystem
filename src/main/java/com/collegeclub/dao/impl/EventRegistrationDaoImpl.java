package com.collegeclub.dao.impl;

import com.collegeclub.dao.EventRegistrationDao;
import com.collegeclub.model.Event;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationDaoImpl implements EventRegistrationDao {

    @Override
    public boolean register(int eventId, int userId) throws SQLException {
        String sql = "INSERT INTO event_registrations (event_id, user_id, registered_at, status) VALUES (?, ?, ?, 'CONFIRMED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancel(int eventId, int userId) throws SQLException {
        String sql = "DELETE FROM event_registrations WHERE event_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isRegistered(int eventId, int userId) throws SQLException {
        String sql = "SELECT id FROM event_registrations WHERE event_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int countRegistrations(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public List<Event> findEventsByUserId(int userId) throws SQLException {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT e.id, e.club_id, e.title, e.description, e.event_date, e.venue, e.capacity, " +
                "c.name AS club_name, " +
                "(SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count " +
                "FROM events e LEFT JOIN clubs c ON e.club_id = c.id " +
                "INNER JOIN event_registrations er ON e.id = er.event_id " +
                "WHERE er.user_id = ? ORDER BY e.event_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    list.add(e);
                }
            }
        }
        return list;
    }

    @Override
    public List<User> findAttendeesByEventId(int eventId) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password_hash, u.email, u.full_name, u.role, u.created_at " +
                "FROM users u INNER JOIN event_registrations er ON u.id = er.user_id " +
                "WHERE er.event_id = ? ORDER BY u.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setEmail(rs.getString("email"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(Role.fromString(rs.getString("role")));
                    u.setCreatedAt(rs.getString("created_at"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    @Override
    public int countTotalRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) FROM event_registrations";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
