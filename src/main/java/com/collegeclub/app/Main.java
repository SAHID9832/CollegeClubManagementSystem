package com.collegeclub.app;

import com.collegeclub.exception.AuthenticationException;
import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Student;
import com.collegeclub.model.User;
import com.collegeclub.service.ClubOperations;
import com.collegeclub.service.EventOperations;
import com.collegeclub.service.UserOperations;
import com.collegeclub.service.impl.ClubService;
import com.collegeclub.service.impl.EventService;
import com.collegeclub.service.impl.UserService;
import com.collegeclub.util.ConfigLoader;

/**
 * Preserved and Enhanced Faculty Phase 2 Console Program.
 * Demonstrates:
 * - Packages (model, service, exception, app, dao, util)
 * - UserOperations interface and polymorphic UserService implementation
 * - Real user authentication with PBKDF2 hashing replacing hardcoded credentials
 * - Exception handling and custom exception hierarchies
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       COLLEGE CLUB MANAGEMENT SYSTEM");
        System.out.println("              PHASE 1 & PHASE 2");
        System.out.println("==================================================");

        // -------------------------------
        // PHASE 1: Entity Instantiation & Encapsulation
        // -------------------------------
        Student student = new Student(
                101,
                "rahulkumar",
                "pbkdf2$hash",
                "rahul@college.edu",
                "Rahul Kumar",
                "26MCAB32",
                "MCA",
                1
        );

        Club club = new Club(
                1,
                "Coding Club",
                "A club for programming and technology enthusiasts.",
                "Technical",
                "Dr. Arul",
                60
        );

        Event event = new Event(
                101,
                1,
                "Coding Competition",
                "Algorithmic challenges and coding rounds",
                "2026-10-25 10:00",
                "Computer Lab 2",
                50
        );

        System.out.println("\n[PHASE 1 - ENTITY DEMONSTRATION]");
        System.out.println("Student : " + student.getFullName() + " (" + student.getStudentId() + ")");
        System.out.println("Club    : " + club.getName() + " [Coordinator: " + club.getCoordinator() + "]");
        System.out.println("Event   : " + event.getTitle() + " [Venue: " + event.getVenue() + "]");

        // -------------------------------
        // PHASE 2 - USER SERVICE & INTERFACE
        // -------------------------------
        System.out.println("\n[PHASE 2 - USER OPERATIONS VIA INTERFACE]");
        UserOperations userOps = new UserService();

        // 1. Format validation check
        boolean validFormat = userOps.validateUser("sheeja", "pass1234");
        System.out.println("Format Validation ('sheeja', 'pass1234'): " + (validFormat ? "VALID" : "INVALID"));

        boolean invalidFormat = userOps.validateUser("abc", "pass1234");
        System.out.println("Format Validation ('abc', 'pass1234')   : " + (invalidFormat ? "VALID" : "INVALID (expected min 5 chars)"));

        // 2. Real Login against Database
        String adminPass = ConfigLoader.getAdminPassword();
        try {
            User loggedIn = userOps.login("admin", adminPass);
            System.out.println("Login Successful for user: " + loggedIn.getUsername() + " [Role: " + loggedIn.getRole() + "]");
        } catch (AuthenticationException e) {
            System.out.println("Login Failed: " + e.getMessage());
        }

        // 3. Failed Login with wrong password
        try {
            userOps.login("admin", "wrong_password");
            System.out.println("Login unexpectedly succeeded!");
        } catch (AuthenticationException e) {
            System.out.println("Expected Login Failure: " + e.getMessage());
        }

        // -------------------------------
        // PHASE 2 - CLUB SERVICE
        // -------------------------------
        System.out.println("\n[PHASE 2 - CLUB SERVICE OPERATIONS]");
        ClubOperations clubOps = new ClubService();
        try {
            try {
                clubOps.createClub(club);
            } catch (Exception ignored) {} // may already exist in DB

            Club retrieved = clubOps.getClubById(club.getId());
            System.out.println("Club Found: " + retrieved.getName());

            // Registration of user in DB to allow join
            try {
                userOps.registerUser(student, "Pass1234");
            } catch (Exception ignored) {}

            User studentInDb = userOps.findByUsername(student.getUsername());
            clubOps.joinClub(club.getId(), studentInDb.getId());
            System.out.println("Student successfully joined " + retrieved.getName() + ".");

        } catch (Exception e) {
            System.out.println("Club Operation Info: " + e.getMessage());
        }

        // -------------------------------
        // PHASE 2 - EVENT SERVICE
        // -------------------------------
        System.out.println("\n[PHASE 2 - EVENT SERVICE OPERATIONS]");
        EventOperations eventOps = new EventService();
        try {
            try {
                eventOps.createEvent(event);
            } catch (Exception ignored) {}

            User studentInDb = userOps.findByUsername(student.getUsername());
            eventOps.registerForEvent(event.getId(), studentInDb.getId());
            System.out.println("Student successfully registered for " + event.getTitle() + ".");

        } catch (Exception e) {
            System.out.println("Event Operation Info: " + e.getMessage());
        }

        System.out.println("\n==================================================");
        System.out.println("       PHASE 1 & PHASE 2 COMPLETED SUCCESSFULLY");
        System.out.println("==================================================");
    }
}
