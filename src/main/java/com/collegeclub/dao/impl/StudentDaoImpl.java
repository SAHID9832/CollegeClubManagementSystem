package com.collegeclub.dao.impl;

import com.collegeclub.dao.StudentDao;
import com.collegeclub.model.Student;
import com.collegeclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {

    @Override
    public Student save(Student student) throws SQLException {
        // Upsert student record
        String checkSql = "SELECT id FROM students WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, student.getId());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    // Update
                    String updateSql = "UPDATE students SET student_id = ?, department = ?, course = ?, study_year = ? WHERE user_id = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, student.getRollNumber());
                        updatePs.setString(2, student.getDepartment());
                        updatePs.setString(3, student.getCourse());
                        updatePs.setInt(4, student.getYear());
                        updatePs.setInt(5, student.getId());
                        updatePs.executeUpdate();
                    }
                } else {
                    // Insert
                    String insertSql = "INSERT INTO students (user_id, student_id, department, course, study_year) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, student.getId());
                        insertPs.setString(2, student.getRollNumber());
                        insertPs.setString(3, student.getDepartment());
                        insertPs.setString(4, student.getCourse());
                        insertPs.setInt(5, student.getYear());
                        insertPs.executeUpdate();
                    }
                }
            }
        }
        return student;
    }

    @Override
    public Optional<Student> findByUserId(int userId) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password_hash, u.email, u.full_name, " +
                "s.student_id, s.department, s.course, s.study_year " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id " +
                "WHERE u.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT u.id, u.username, u.password_hash, u.email, u.full_name, " +
                "s.student_id, s.department, s.course, s.study_year " +
                "FROM users u " +
                "JOIN students s ON u.id = s.user_id " +
                "WHERE LOWER(s.student_id) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateProfile(int userId, String fullName, String email, String department, String course, int year) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateUserSql = "UPDATE users SET full_name = ?, email = ? WHERE id = ?";
                try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
                    psUser.setString(1, fullName != null ? fullName.trim() : "");
                    psUser.setString(2, email != null ? email.trim() : "");
                    psUser.setInt(3, userId);
                    psUser.executeUpdate();
                }

                String checkSql = "SELECT id FROM students WHERE user_id = ?";
                boolean exists;
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setInt(1, userId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        exists = rs.next();
                    }
                }

                if (exists) {
                    String updateStudentSql = "UPDATE students SET department = ?, course = ?, study_year = ? WHERE user_id = ?";
                    try (PreparedStatement psStud = conn.prepareStatement(updateStudentSql)) {
                        psStud.setString(1, department != null ? department.trim() : "");
                        psStud.setString(2, course != null ? course.trim() : "MCA");
                        psStud.setInt(3, year > 0 ? year : 1);
                        psStud.setInt(4, userId);
                        psStud.executeUpdate();
                    }
                } else {
                    String insertStudentSql = "INSERT INTO students (user_id, student_id, department, course, study_year) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement psStud = conn.prepareStatement(insertStudentSql)) {
                        psStud.setInt(1, userId);
                        psStud.setString(2, "26MCA" + userId);
                        psStud.setString(3, department != null ? department.trim() : "");
                        psStud.setString(4, course != null ? course.trim() : "MCA");
                        psStud.setInt(5, year > 0 ? year : 1);
                        psStud.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password_hash, u.email, u.full_name, " +
                "s.student_id, s.department, s.course, s.study_year " +
                "FROM users u " +
                "LEFT JOIN students s ON u.id = s.user_id " +
                "WHERE u.role = 'STUDENT' ORDER BY u.id ASC";
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
    public int countTotalStudents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'STUDENT'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passHash = rs.getString("password_hash");
        String email = rs.getString("email");
        String fullName = rs.getString("full_name");
        String rollNumber = rs.getString("student_id");
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            rollNumber = "26MCA" + String.format("%03d", id);
        }
        String department = rs.getString("department");
        if (department == null || department.trim().isEmpty()) {
            department = "Computer Applications";
        }
        String course = rs.getString("course");
        if (course == null || course.trim().isEmpty()) {
            course = "MCA";
        }
        int studyYear = rs.getInt("study_year");
        if (studyYear <= 0) {
            studyYear = 1;
        }

        return new Student(id, username, passHash, email, fullName, rollNumber, department, course, studyYear);
    }
}
