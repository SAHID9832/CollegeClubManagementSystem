package com.collegeclub.dao.impl;

import com.collegeclub.dao.MembershipDao;
import com.collegeclub.model.Club;
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

public class MembershipDaoImpl implements MembershipDao {

    @Override
    public boolean addMembership(int clubId, int userId) throws SQLException {
        String sql = "INSERT INTO club_members (club_id, user_id, joined_at, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, userId);
            ps.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean removeMembership(int clubId, int userId) throws SQLException {
        String sql = "DELETE FROM club_members WHERE club_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean isMember(int clubId, int userId) throws SQLException {
        String sql = "SELECT id FROM club_members WHERE club_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int countMembers(int clubId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM club_members WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public List<Club> findClubsByUserId(int userId) throws SQLException {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.description, c.category, c.coordinator, c.capacity, c.active, " +
                "(SELECT COUNT(*) FROM club_members m WHERE m.club_id = c.id) AS member_count " +
                "FROM clubs c INNER JOIN club_members cm ON c.id = cm.club_id " +
                "WHERE cm.user_id = ? ORDER BY c.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Club c = new Club();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));
                    c.setCategory(rs.getString("category"));
                    c.setCoordinator(rs.getString("coordinator"));
                    c.setCapacity(rs.getInt("capacity"));
                    c.setActive(rs.getBoolean("active"));
                    c.setMemberCount(rs.getInt("member_count"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    @Override
    public List<User> findMembersByClubId(int clubId) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password_hash, u.email, u.full_name, u.role, u.created_at " +
                "FROM users u INNER JOIN club_members cm ON u.id = cm.user_id " +
                "WHERE cm.club_id = ? ORDER BY u.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
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
}
