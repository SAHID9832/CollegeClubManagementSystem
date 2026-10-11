package com.collegeclub.dao.impl;

import com.collegeclub.dao.ClubDao;
import com.collegeclub.model.Club;
import com.collegeclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubDaoImpl implements ClubDao {

    @Override
    public Club save(Club club) throws SQLException {
        String sql = "INSERT INTO clubs (name, description, category, coordinator, capacity, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, club.getName());
            ps.setString(2, club.getDescription());
            ps.setString(3, club.getCategory());
            ps.setString(4, club.getCoordinator());
            ps.setInt(5, club.getCapacity());
            ps.setBoolean(6, club.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    club.setId(rs.getInt(1));
                }
            }
            return club;
        }
    }

    @Override
    public Optional<Club> findById(int id) throws SQLException {
        String sql = "SELECT c.id, c.name, c.description, c.category, c.coordinator, c.capacity, c.active, " +
                "(SELECT COUNT(*) FROM club_members m WHERE m.club_id = c.id) AS member_count " +
                "FROM clubs c WHERE c.id = ?";
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
    public Optional<Club> findByName(String name) throws SQLException {
        String sql = "SELECT c.id, c.name, c.description, c.category, c.coordinator, c.capacity, c.active, " +
                "(SELECT COUNT(*) FROM club_members m WHERE m.club_id = c.id) AS member_count " +
                "FROM clubs c WHERE c.name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Club> findAll() throws SQLException {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.description, c.category, c.coordinator, c.capacity, c.active, " +
                "(SELECT COUNT(*) FROM club_members m WHERE m.club_id = c.id) AS member_count " +
                "FROM clubs c WHERE c.active = TRUE ORDER BY c.id ASC";
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
    public List<Club> search(String query, String category) throws SQLException {
        List<Club> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT c.id, c.name, c.description, c.category, c.coordinator, c.capacity, c.active, " +
                "(SELECT COUNT(*) FROM club_members m WHERE m.club_id = c.id) AS member_count " +
                "FROM clubs c WHERE c.active = TRUE ");

        List<Object> params = new ArrayList<>();
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(c.name) LIKE ? OR LOWER(c.description) LIKE ? OR LOWER(c.coordinator) LIKE ?) ");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            sql.append("AND c.category = ? ");
            params.add(category.trim());
        }
        sql.append("ORDER BY c.id ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean update(Club club) throws SQLException {
        String sql = "UPDATE clubs SET name = ?, description = ?, category = ?, coordinator = ?, capacity = ?, active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, club.getName());
            ps.setString(2, club.getDescription());
            ps.setString(3, club.getCategory());
            ps.setString(4, club.getCoordinator());
            ps.setInt(5, club.getCapacity());
            ps.setBoolean(6, club.isActive());
            ps.setInt(7, club.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM clubs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int countTotalClubs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM clubs WHERE active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Club> getAllClubs() {
        try {
            return findAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    private Club mapRow(ResultSet rs) throws SQLException {
        Club c = new Club();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setCategory(rs.getString("category"));
        c.setCoordinator(rs.getString("coordinator"));
        c.setCapacity(rs.getInt("capacity"));
        c.setActive(rs.getBoolean("active"));
        c.setMemberCount(rs.getInt("member_count"));
        return c;
    }
}
