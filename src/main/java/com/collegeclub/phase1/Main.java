package com.collegeclub.phase1;

import java.util.Scanner;

/**
 * Faculty Reference Phase 1 Main Application (Module 3).
 * Demonstrates:
 * 1. User registration flow with Scanner input
 * 2. String-based validation via Validation class (Module 2)
 * 3. Encapsulation & constructors via User (Module 1), Club, Event, and Registration
 * 4. displayX() methods for all entities
 * 5. Comprehensive automated test harness for faculty demonstration
 */
public class Main {

    public static boolean register(String username, String password) {
        System.out.println("Attempting Registration for Username: '" + username + "'");
        try {
            Validation.validateUsername(username);
            Validation.validatePassword(password);
            User user = new User(username, password);
            System.out.println("\nRegistration Successful!");
            user.displayUser();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("\nRegistration Failed.");
            return false;
        }
    }

    public static void runDemonstration() {
        System.out.println("============================================================");
        System.out.println("   CAMPUSSPHERE - COLLEGE CLUB MANAGEMENT SYSTEM (PHASE 1)");
        System.out.println("                  AUTOMATED VERIFICATION DEMO");
        System.out.println("============================================================");

        // --- Test Case 1: Valid Registration ---
        System.out.println("\n[TEST CASE 1: Valid Student Registration]");
        boolean test1 = register("rahulkumar", "Pass1234");
        System.out.println("Result: " + (test1 ? "PASS" : "FAIL"));

        // --- Entity Demonstrations ---
        System.out.println("\n[ENTITY DEMONSTRATION: Club Entity]");
        Club club = new Club(1, "Coding Club", "Technical", "Dr. Arul", 60);
        club.setMemberCount(25);
        club.displayClub();

        System.out.println("\n[ENTITY DEMONSTRATION: Event Entity]");
        Event event = new Event(
                101,
                "Hackathon 2026",
                "Coding Club",
                "Technical",
                "2026-10-25 10:00 AM",
                "Computer Lab 2",
                50
        );
        event.setRegisteredCount(18);
        event.displayEvent();

        System.out.println("\n[ENTITY DEMONSTRATION: Registration Entity]");
        Registration registration = new Registration(
                1001,
                "rahulkumar",
                "Hackathon 2026",
                "2026-10-12",
                "CONFIRMED"
        );
        registration.displayRegistration();

        // --- Negative Test Cases (Faculty Requirement) ---
        System.out.println("\n============================================================");
        System.out.println("               NEGATIVE VALIDATION TESTS");
        System.out.println("============================================================");

        System.out.println("\n[TEST CASE 2: Short Username (< 5 chars)]");
        boolean test2 = register("rah", "Pass1234");
        System.out.println("Expected: Failure (Username should contain at least 5 characters.) | Actual: " + (!test2 ? "PASS" : "FAIL"));

        System.out.println("\n[TEST CASE 3: Empty Username]");
        boolean test3 = register("   ", "Pass1234");
        System.out.println("Expected: Failure (Username cannot be empty.) | Actual: " + (!test3 ? "PASS" : "FAIL"));

        System.out.println("\n[TEST CASE 4: Short Password (< 8 chars)]");
        boolean test4 = register("rahulkumar", "pass1");
        System.out.println("Expected: Failure (Password should contain at least 8 characters.) | Actual: " + (!test4 ? "PASS" : "FAIL"));

        System.out.println("\n[TEST CASE 5: Password Missing Digit]");
        boolean test5 = register("rahulkumar", "password");
        System.out.println("Expected: Failure (Password must contain at least one digit.) | Actual: " + (!test5 ? "PASS" : "FAIL"));

        System.out.println("\n============================================================");
        System.out.println("         PHASE 1 VERIFICATION COMPLETED SUCCESSFULLY        ");
        System.out.println("============================================================");
    }

    public static void main(String[] args) {
        // Direct command line arguments check (for scripted testing)
        if (args != null && args.length >= 2) {
            System.out.println("====== User Registration System ======");
            register(args[0], args[1]);
            return;
        }

        // Demo flag or non-interactive execution
        if (args != null && args.length == 1 && "--demo".equalsIgnoreCase(args[0])) {
            runDemonstration();
            return;
        }

        // Check if console is available for interactive scanner execution
        if (System.console() == null) {
            // When run via Maven exec or non-interactive shell, run the full verification demo
            runDemonstration();
            return;
        }

        // Interactive Scanner Execution (Matching Faculty Lab Program exactly)
        Scanner sc = new Scanner(System.in);
        System.out.println("====== User Registration System ======");

        System.out.print("Enter Username : ");
        String username = sc.nextLine();

        System.out.print("Enter Password : ");
        String password = sc.nextLine();

        if (register(username, password)) {
            // Demonstrating expanded Club, Event, Registration models
            Club club = new Club(1, "Coding Club", "Technical", "Dr. Arul", 60);
            club.displayClub();

            Event event = new Event(
                    101,
                    "Hackathon 2026",
                    "Coding Club",
                    "Technical",
                    "2026-10-25 10:00 AM",
                    "Computer Lab 2",
                    50
            );
            event.displayEvent();

            Registration reg = new Registration(1001, username, "Hackathon 2026", "2026-10-12", "CONFIRMED");
            reg.displayRegistration();
        }

        sc.close();
    }
}
