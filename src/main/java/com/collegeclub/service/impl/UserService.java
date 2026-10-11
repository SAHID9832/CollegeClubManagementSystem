package com.collegeclub.service.impl;

import com.collegeclub.dao.UserDao;
import com.collegeclub.dao.impl.UserDaoImpl;
import com.collegeclub.exception.AuthenticationException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateUserException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.service.UserOperations;
import com.collegeclub.util.ConfigLoader;
import com.collegeclub.util.PasswordUtil;
import com.collegeclub.util.Validation;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service implementation for User operations.
 * Implements UserOperations interface.
 * Replaces hardcoded admin login with secure database lookups and PBKDF2 hashing.
 */
public class UserService implements UserOperations {

    private final UserDao userDao;

    public UserService() {
        this(new UserDaoImpl());
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        seedDefaultAdmin();
    }

    @Override
    public boolean validateUser(String username, String password) {
        try {
            Validation.validateUsername(username);
            Validation.validatePassword(password);
            return true;
        } catch (InvalidInputException e) {
            return false;
        }
    }

    @Override
    public User login(String username, String password) throws AuthenticationException {
        if (!validateUser(username, password)) {
            throw new AuthenticationException("Invalid username or password.");
        }

        try {
            Optional<User> userOpt = userDao.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new AuthenticationException("Invalid username or password.");
            }

            User user = userOpt.get();
            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                throw new AuthenticationException("Invalid username or password.");
            }

            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during authentication: " + e.getMessage());
        }
    }

    @Override
    public User registerUser(User user, String plainPassword) throws InvalidInputException, DuplicateUserException, ClubSystemException {
        if (user == null) {
            throw new InvalidInputException("User data cannot be null.");
        }

        Validation.validateUsername(user.getUsername());
        Validation.validatePassword(plainPassword);
        Validation.validateEmail(user.getEmail());
        Validation.validateFullName(user.getFullName());

        try {
            if (userDao.findByUsername(user.getUsername()).isPresent()) {
                throw new DuplicateUserException("Username '" + user.getUsername() + "' is already registered.");
            }

            if (userDao.findByEmail(user.getEmail()).isPresent()) {
                throw new DuplicateUserException("Email '" + user.getEmail() + "' is already registered.");
            }

            String hash = PasswordUtil.hashPassword(plainPassword);
            user.setPasswordHash(hash);

            return userDao.save(user);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Failed to register user: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public User findByUsername(String username) throws ClubSystemException {
        try {
            return userDao.findByUsername(username)
                    .orElseThrow(() -> new ClubSystemException("USER_NOT_FOUND", "User not found: " + username, 404));
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error looking up user: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws ClubSystemException {
        Validation.validatePassword(newPassword);
        try {
            User user = userDao.findById(userId)
                    .orElseThrow(() -> new ClubSystemException("USER_NOT_FOUND", "User not found", 404));

            if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
                throw new AuthenticationException("Current password does not match.");
            }

            String newHash = PasswordUtil.hashPassword(newPassword);
            return userDao.updatePassword(userId, newHash);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error updating password: " + e.getMessage(), 500, e);
        }
    }

    private void seedDefaultAdmin() {
        try {
            if (userDao.findByUsername("admin").isEmpty()) {
                String adminPass = ConfigLoader.getAdminPassword();
                User admin = new User(
                        "admin",
                        PasswordUtil.hashPassword(adminPass),
                        "admin@college.edu",
                        "System Administrator",
                        Role.ADMIN
                );
                userDao.save(admin);
            }
        } catch (Exception e) {
            // Seeding warning during tests/startup
            System.err.println("Notice: Default admin seeding status: " + e.getMessage());
        }
    }
}
