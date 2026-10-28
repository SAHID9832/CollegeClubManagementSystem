package com.collegeclub.dao;

import com.collegeclub.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user) throws SQLException;
    Optional<User> findById(int id) throws SQLException;
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    List<User> findAll() throws SQLException;
    boolean updatePassword(int id, String newPasswordHash) throws SQLException;
    boolean updateUserRole(int id, com.collegeclub.model.Role role) throws SQLException;
    boolean updateUserStatus(int id, boolean active) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countTotalStudents() throws SQLException;
    List<User> getAllUsers();
}
