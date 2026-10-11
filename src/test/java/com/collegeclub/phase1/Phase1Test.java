package com.collegeclub.phase1;

import com.collegeclub.model.Admin;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Role;
import com.collegeclub.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 1 Acceptance and Model Tests")
public class Phase1Test {

    @Test
    @DisplayName("Acceptance Case 1: sheeja / pass1234 -> Registration Successful!")
    void testValidRegistration() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            boolean success = Main.register("sheeja", "pass1234");
            assertTrue(success, "Valid registration should succeed");
            String output = outContent.toString();
            assertTrue(output.contains("Registration Successful!"), "Output should contain success message");
            assertTrue(output.contains("Username : sheeja"), "Output should display username");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Acceptance Case 2: abc / pass1234 -> Username should contain at least 5 characters. Registration Failed.")
    void testShortUsername() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            boolean success = Main.register("abc", "pass1234");
            assertFalse(success, "Short username should fail");
            String output = outContent.toString();
            assertTrue(output.contains("Username should contain at least 5 characters."),
                    "Output should contain minimum character message");
            assertTrue(output.contains("Registration Failed."), "Output should state failure");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Acceptance Case 3: sheeja / password -> Password must contain at least one digit.")
    void testPasswordMissingDigit() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            boolean success = Main.register("sheeja", "password");
            assertFalse(success, "Password without digit should fail");
            String output = outContent.toString();
            assertTrue(output.contains("Password must contain at least one digit."),
                    "Output should specify missing digit message");
            assertTrue(output.contains("Registration Failed."), "Output should state failure");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Acceptance Case 4: empty username -> Username cannot be empty.")
    void testEmptyUsername() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            boolean success = Main.register("   ", "pass1234");
            assertFalse(success, "Blank username should fail");
            String output = outContent.toString();
            assertTrue(output.contains("Username cannot be empty."),
                    "Output should contain cannot be empty message");
            assertTrue(output.contains("Registration Failed."), "Output should state failure");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Acceptance Case 5: Direct Validation class checks")
    void testValidationDirectMethods() {
        assertDoesNotThrow(() -> Validation.validateUsername("sheeja"));
        assertDoesNotThrow(() -> Validation.validatePassword("pass1234"));

        Exception ex1 = assertThrows(Exception.class, () -> Validation.validateUsername(""));
        assertEquals("Username cannot be empty.", ex1.getMessage());

        Exception ex2 = assertThrows(Exception.class, () -> Validation.validateUsername("john"));
        assertEquals("Username should contain at least 5 characters.", ex2.getMessage());

        Exception ex3 = assertThrows(Exception.class, () -> Validation.validatePassword(""));
        assertEquals("Password cannot be empty.", ex3.getMessage());

        Exception ex4 = assertThrows(Exception.class, () -> Validation.validatePassword("pass1"));
        assertEquals("Password should contain at least 8 characters.", ex4.getMessage());

        Exception ex5 = assertThrows(Exception.class, () -> Validation.validatePassword("password"));
        assertEquals("Password must contain at least one digit.", ex5.getMessage());
    }

    @Test
    @DisplayName("Model Hierarchy & Encapsulation: Student and Admin inheritance")
    void testInheritanceAndEncapsulation() {
        Student student = new Student(1, "rahul", "pbkdf2$hash", "rahul@college.edu", "Rahul Kumar", "26MCAB32", "MCA", 1);
        assertEquals("26MCAB32", student.getStudentId());
        assertEquals("MCA", student.getDepartment());
        assertEquals(Role.STUDENT, student.getRole());

        Admin admin = new Admin(2, "admin_user", "pbkdf2$hash", "admin@college.edu", "Campus Admin", "SUPER_ADMIN", "Admin Block 204");
        assertEquals("SUPER_ADMIN", admin.getAdminLevel());
        assertEquals(Role.ADMIN, admin.getRole());

        // Verify displayUser never exposes password hash
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream orig = System.out;
        System.setOut(new PrintStream(out));
        try {
            student.displayUser();
            String res = out.toString();
            assertFalse(res.contains("pbkdf2"), "displayUser must NEVER output password or hash");
            assertTrue(res.contains("Rahul Kumar"));
        } finally {
            System.setOut(orig);
        }
    }

    @Test
    @DisplayName("Club & Event Encapsulation and Validation Rules")
    void testClubAndEventEncapsulation() {
        Club club = new Club(1, "Coding Club", "Tech and Coding", "Technical", "Dr. Arul", 60);
        assertEquals("Coding Club", club.getName());
        assertEquals(60, club.getCapacity());

        assertThrows(IllegalArgumentException.class, () -> club.setName("ab"));
        assertThrows(IllegalArgumentException.class, () -> club.setCoordinator(""));
        assertThrows(IllegalArgumentException.class, () -> club.setCapacity(0));

        Event event = new Event(101, 1, "Hackathon 2026", "24hr Coding Event", "2026-10-25 09:00", "Auditorium", 100);
        assertEquals("Hackathon 2026", event.getTitle());
        assertEquals(100, event.getSeatsLeft());

        assertThrows(IllegalArgumentException.class, () -> event.setTitle("ab"));
        assertThrows(IllegalArgumentException.class, () -> event.setEventDate(""));
        assertThrows(IllegalArgumentException.class, () -> event.setVenue(""));
    }
}
