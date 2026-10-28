package com.collegeclub.service.impl;

import com.collegeclub.dao.NotificationDao;
import com.collegeclub.dao.StudentDao;
import com.collegeclub.dao.UserDao;
import com.collegeclub.dao.impl.NotificationDaoImpl;
import com.collegeclub.dao.impl.StudentDaoImpl;
import com.collegeclub.dao.impl.UserDaoImpl;
import com.collegeclub.exception.AuthenticationException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateUserException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.Notification;
import com.collegeclub.model.Role;
import com.collegeclub.model.Student;
import com.collegeclub.model.User;
import com.collegeclub.service.UserOperations;
import com.collegeclub.util.ConfigLoader;
import com.collegeclub.util.PasswordUtil;
import com.collegeclub.util.Validation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for User operations.
 * Implements UserOperations interface.
 * Provides secure database persistence with PBKDF2 hashing, student profiles,
 * and notifications.
 */
public class UserService implements UserOperations {

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final NotificationDao notificationDao;

    public UserService() {
        this(new UserDaoImpl(), new StudentDaoImpl(), new NotificationDaoImpl());
    }

    public UserService(UserDao userDao) {
        this(userDao, new StudentDaoImpl(), new NotificationDaoImpl());
    }

    public UserService(UserDao userDao, StudentDao studentDao, NotificationDao notificationDao) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.notificationDao = notificationDao;
        seedDefaultAdmin();
    }

    @Override
    public boolean validateUser(String username, String password) {
        try {
            Validation.validateUsername(username);
            Validation.validatePassword(password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User login(String username, String password) throws AuthenticationException {
        try {
            Validation.validateUsername(username);
            Validation.validatePassword(password);
        } catch (InvalidInputException e) {
            throw new AuthenticationException(e.getMessage());
        }

        try {
            User user = userDao.findByUsername(username)
                    .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

            if (!user.isActive()) {
                throw new AuthenticationException("Your account has been disabled by the administrator. Please contact support.");
            }

            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                throw new AuthenticationException("Invalid username or password.");
            }

            return user;
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during login: " + e.getMessage());
        }
    }

    public User authenticate(String username, String password) throws AuthenticationException, ClubSystemException {
        return login(username, password);
    }

    @Override
    public User registerUser(User user, String plainPassword)
            throws InvalidInputException, DuplicateUserException, ClubSystemException {
        if (user == null) {
            throw new InvalidInputException("User data cannot be null.");
        }
        Validation.validateUsername(user.getUsername());
        Validation.validatePassword(plainPassword);

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidInputException("A valid email address is required.");
        }

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
            throw new ClubSystemException("DATABASE_ERROR", "Error registering user: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public User findByUsername(String username) throws ClubSystemException {
        try {
            return userDao.findByUsername(username).orElse(null);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving user: " + e.getMessage(), 500, e);
        }
    }

    public Optional<User> findUserOptional(String username) {
        try {
            return userDao.findByUsername(username);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword)
            throws InvalidInputException, AuthenticationException, ClubSystemException {
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

    public Student registerStudent(String username, String password, String email, String fullName,
                                   String rollNumber, String department, String course, int year)
            throws InvalidInputException, DuplicateUserException, ClubSystemException {
        User user = new User(username, null, email, fullName, Role.STUDENT);
        User savedUser = registerUser(user, password);

        String finalRoll = (rollNumber != null && !rollNumber.trim().isEmpty())
                ? rollNumber.trim()
                : "26MCA" + String.format("%03d", savedUser.getId());
        String finalDept = (department != null && !department.trim().isEmpty())
                ? department.trim()
                : "Computer Applications";
        String finalCourse = (course != null && !course.trim().isEmpty())
                ? course.trim()
                : "MCA";
        int finalYear = year > 0 ? year : 1;

        Student student = new Student(savedUser.getId(), savedUser.getUsername(), savedUser.getPasswordHash(),
                savedUser.getEmail(), savedUser.getFullName(), finalRoll, finalDept, finalCourse, finalYear);

        try {
            studentDao.save(student);
            // Send welcome notification
            Notification welcomeNotif = new Notification(
                    savedUser.getId(),
                    "Welcome to Campus Clubs!",
                    "Your student profile has been created. Explore 30+ clubs and register for upcoming events.",
                    "INFO"
            );
            notificationDao.save(welcomeNotif);
        } catch (SQLException e) {
            System.err.println("Notice: Student profile creation warning: " + e.getMessage());
        }

        return student;
    }

    public Student getStudentByUserId(int userId) throws ClubSystemException {
        try {
            Optional<Student> studOpt = studentDao.findByUserId(userId);
            if (studOpt.isPresent()) {
                return studOpt.get();
            }

            User u = userDao.findById(userId)
                    .orElseThrow(() -> new ClubSystemException("USER_NOT_FOUND", "User not found: " + userId, 404));
            Student fallback = new Student(u.getId(), u.getUsername(), u.getPasswordHash(), u.getEmail(),
                    u.getFullName(), "26MCA" + String.format("%03d", u.getId()), "Computer Applications", "MCA", 1);
            try {
                studentDao.save(fallback);
            } catch (SQLException ignored) {}
            return fallback;
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving student: " + e.getMessage(), 500, e);
        }
    }

    public boolean updateStudentProfile(int userId, String fullName, String email, String department, String course, int year)
            throws ClubSystemException {
        try {
            return studentDao.updateProfile(userId, fullName, email, department, course, year);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error updating student profile: " + e.getMessage(), 500, e);
        }
    }

    public List<Notification> getNotifications(int userId) throws ClubSystemException {
        try {
            List<Notification> list = notificationDao.findByUserId(userId);
            if (list.isEmpty()) {
                // If user has no notifications yet, give them an initial welcome alert
                Notification n = new Notification(userId, "Welcome to Campus Clubs!",
                        "Explore 30+ campus clubs and enroll in workshops across technology, business, arts and sports.", "INFO");
                notificationDao.save(n);
                list.add(n);
            }
            return list;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    public boolean markNotificationRead(int notificationId, int userId) throws ClubSystemException {
        try {
            return notificationDao.markAsRead(notificationId, userId);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean markAllNotificationsRead(int userId) throws ClubSystemException {
        try {
            return notificationDao.markAllAsRead(userId);
        } catch (SQLException e) {
            return false;
        }
    }

    private void seedDefaultAdmin() {
        try {
            String adminUser = ConfigLoader.getAdminUsername();
            String adminPass = ConfigLoader.getAdminPassword();
            Optional<User> existing = userDao.findByUsername(adminUser);
            if (existing.isEmpty()) {
                User admin = new User(
                        adminUser,
                        PasswordUtil.hashPassword(adminPass),
                        "admin@college.edu",
                        "System Administrator",
                        Role.ADMIN,
                        true
                );
                userDao.save(admin);
            } else {
                User admin = existing.get();
                if (admin.getRole() != Role.ADMIN || !admin.isActive()) {
                    userDao.updateUserRole(admin.getId(), Role.ADMIN);
                    userDao.updateUserStatus(admin.getId(), true);
                }
            }

            // Seed / Ensure default student 'sahid' with Password@123
            Optional<User> studentExisting = userDao.findByUsername("sahid");
            if (studentExisting.isEmpty()) {
                User s = new User(
                        "sahid",
                        PasswordUtil.hashPassword("Password@123"),
                        "sahid@college.edu",
                        "Sahid Student",
                        Role.STUDENT,
                        true
                );
                userDao.save(s);
            } else {
                User s = studentExisting.get();
                userDao.updatePassword(s.getId(), PasswordUtil.hashPassword("Password@123"));
                userDao.updateUserStatus(s.getId(), true);
            }
        } catch (Exception e) {
            System.err.println("Notice: Default admin and student seeding status: " + e.getMessage());
        }
    }
}
