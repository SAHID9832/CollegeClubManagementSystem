package com.collegeclub.service;

import com.collegeclub.exception.AuthenticationException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateUserException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.User;

/**
 * Faculty Reference Service Interface: UserOperations.
 * Preserves validateUser(username, password) and login(username, password)
 * while extending with registration, profile retrieval, and password change operations.
 */
public interface UserOperations {

    /**
     * Preserved faculty method to validate credentials format.
     */
    boolean validateUser(String username, String password);

    /**
     * Authenticates a user against secure PBKDF2 hash lookups in the database.
     */
    User login(String username, String password) throws AuthenticationException;

    /**
     * Registers a new user with password hashing and uniqueness enforcement.
     */
    User registerUser(User user, String plainPassword) throws InvalidInputException, DuplicateUserException, ClubSystemException;

    /**
     * Looks up user by username.
     */
    User findByUsername(String username) throws ClubSystemException;

    /**
     * Changes user password after verifying existing password.
     */
    boolean changePassword(int userId, String oldPassword, String newPassword) throws ClubSystemException;
}
