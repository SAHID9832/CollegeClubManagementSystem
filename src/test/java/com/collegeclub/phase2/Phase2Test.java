package com.collegeclub.phase2;

import com.collegeclub.exception.*;
import com.collegeclub.model.*;
import com.collegeclub.service.ClubOperations;
import com.collegeclub.service.EventOperations;
import com.collegeclub.service.UserOperations;
import com.collegeclub.service.impl.ClubService;
import com.collegeclub.service.impl.EventService;
import com.collegeclub.service.impl.UserService;
import com.collegeclub.util.ConfigLoader;
import com.collegeclub.util.DBConnection;
import com.collegeclub.util.PasswordUtil;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 2 Structured Programming and Reliability Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Phase2Test {

    private static UserOperations userOps;
    private static ClubOperations clubOps;
    private static EventOperations eventOps;

    @BeforeAll
    static void setUp() throws Exception {
        // Use 1,000 iterations for test speed, production uses 600,000
        PasswordUtil.setDefaultIterations(1000);

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            DBConnection.initSchema(conn);
        }

        userOps = new UserService();
        clubOps = new ClubService();
        eventOps = new EventService();
    }

    @AfterAll
    static void tearDown() {
        PasswordUtil.setDefaultIterations(600_000);
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @Order(1)
    @DisplayName("Password Hashing & Verification via PBKDF2")
    void testPasswordHashAndVerify() {
        String raw = "SecurePass123";
        String hash = PasswordUtil.hashPassword(raw);

        assertNotNull(hash);
        assertTrue(hash.startsWith("pbkdf2$1000$"), "Hash should match pbkdf2 format");

        assertTrue(PasswordUtil.verifyPassword(raw, hash), "Correct password must verify");
        assertFalse(PasswordUtil.verifyPassword("WrongPass123", hash), "Wrong password must fail");
        assertFalse(PasswordUtil.verifyPassword(null, hash), "Null password must return false");
        assertFalse(PasswordUtil.verifyPassword(raw, "invalid_hash_format"), "Malformed hash must return false");
    }

    @Test
    @Order(2)
    @DisplayName("Faculty UserOperations Interface: validateUser contract")
    void testFacultyValidateUserContract() {
        assertTrue(userOps.validateUser("sheeja", "pass1234"), "Valid username and password should return true");
        assertFalse(userOps.validateUser("abc", "pass1234"), "Username < 5 chars must return false");
        assertFalse(userOps.validateUser("sheeja", "password"), "Password lacking digit must return false");
        assertFalse(userOps.validateUser("", "pass1234"), "Empty username must return false");
    }

    @Test
    @Order(3)
    @DisplayName("Registration Validation Exceptions")
    void testInvalidRegistrationCases() {
        String sfx = randomSuffix();
        User u1 = new User("abc", "", "test@college.edu", "Test User", Role.STUDENT);
        assertThrows(InvalidUsernameException.class, () -> userOps.registerUser(u1, "pass1234"));

        User u2 = new User("validUser" + sfx, "", "invalid-email", "Test User", Role.STUDENT);
        assertThrows(InvalidInputException.class, () -> userOps.registerUser(u2, "pass1234"));

        User u3 = new User("validUser2" + sfx, "", "test2@college.edu", "Test User", Role.STUDENT);
        assertThrows(InvalidPasswordException.class, () -> userOps.registerUser(u3, "short"));

        User u4 = new User("validUser3" + sfx, "", "test3@college.edu", "Test User", Role.STUDENT);
        assertThrows(InvalidPasswordException.class, () -> userOps.registerUser(u4, "password")); // no digit
    }

    @Test
    @Order(4)
    @DisplayName("Duplicate User and Email Prevention")
    void testDuplicateUserException() throws Exception {
        String sfx = randomSuffix();
        String username = "unique" + sfx;
        String email = "unique" + sfx + "@college.edu";

        User u = new User(username, "", email, "Unique User", Role.STUDENT);
        userOps.registerUser(u, "Secret123");

        // Duplicate username
        User dupUser = new User(username, "", "diff" + sfx + "@college.edu", "Dup User", Role.STUDENT);
        assertThrows(DuplicateUserException.class, () -> userOps.registerUser(dupUser, "Secret123"));

        // Duplicate email
        User dupEmail = new User("other" + sfx, "", email, "Dup Email", Role.STUDENT);
        assertThrows(DuplicateUserException.class, () -> userOps.registerUser(dupEmail, "Secret123"));
    }

    @Test
    @Order(5)
    @DisplayName("Login Success and Authentication Failure")
    void testLoginScenarios() throws Exception {
        String sfx = randomSuffix();
        String username = "loginUser" + sfx;
        User u = new User(username, "", "login" + sfx + "@college.edu", "Login User", Role.STUDENT);
        userOps.registerUser(u, "CorrectPass1");

        // Successful login
        User loggedIn = userOps.login(username, "CorrectPass1");
        assertNotNull(loggedIn);
        assertEquals(username, loggedIn.getUsername());

        // Wrong password throws AuthenticationException
        assertThrows(AuthenticationException.class, () -> userOps.login(username, "WrongPass2"));

        // Non-existent username throws AuthenticationException
        assertThrows(AuthenticationException.class, () -> userOps.login("nonexistent_" + sfx, "CorrectPass1"));
    }

    @Test
    @Order(6)
    @DisplayName("Club Joining and Duplicate Membership Prevention")
    void testClubMembershipOperations() throws Exception {
        String sfx = randomSuffix();
        Club club = new Club(0, "Club " + sfx, "Description", "Technical", "Coordinator", 50);
        Club savedClub = clubOps.createClub(club);

        User student = userOps.registerUser(
                new User("member" + sfx, "", "member" + sfx + "@college.edu", "Member Student", Role.STUDENT),
                "MemberPass1"
        );

        // First join succeeds
        boolean joined = clubOps.joinClub(savedClub.getId(), student.getId());
        assertTrue(joined);

        // Duplicate join throws DuplicateMembershipException
        assertThrows(DuplicateMembershipException.class, () -> clubOps.joinClub(savedClub.getId(), student.getId()));

        // Leave club succeeds
        boolean left = clubOps.leaveClub(savedClub.getId(), student.getId());
        assertTrue(left);
    }

    @Test
    @Order(7)
    @DisplayName("Club Membership Capacity Limit Enforcement")
    void testClubCapacityLimit() throws Exception {
        String sfx = randomSuffix();
        // Club with capacity 1
        Club club = new Club(0, "TinyClub " + sfx, "Tiny club", "Cultural", "Leader", 1);
        Club savedClub = clubOps.createClub(club);

        User u1 = userOps.registerUser(new User("c1_" + sfx, "", "c1_" + sfx + "@edu.in", "User 1", Role.STUDENT), "Pass1234");
        User u2 = userOps.registerUser(new User("c2_" + sfx, "", "c2_" + sfx + "@edu.in", "User 2", Role.STUDENT), "Pass1234");

        // u1 fills the only seat
        assertTrue(clubOps.joinClub(savedClub.getId(), u1.getId()));

        // u2 is rejected with MembershipLimitReachedException
        assertThrows(MembershipLimitReachedException.class, () -> clubOps.joinClub(savedClub.getId(), u2.getId()));
    }

    @Test
    @Order(8)
    @DisplayName("Event Registration, Duplicate Registration & Capacity Full")
    void testEventRegistrationAndCapacity() throws Exception {
        String sfx = randomSuffix();
        Club club = clubOps.createClub(new Club(0, "EvtClub " + sfx, "Club for Events", "Sports", "Lead", 100));

        // Event with capacity 1
        Event event = new Event(0, club.getId(), "Event " + sfx, "Desc", "2026-10-30 10:00", "Field", 1);
        Event savedEvent = eventOps.createEvent(event);

        User u1 = userOps.registerUser(new User("ev1_" + sfx, "", "ev1_" + sfx + "@edu.in", "Event User 1", Role.STUDENT), "Pass1234");
        User u2 = userOps.registerUser(new User("ev2_" + sfx, "", "ev2_" + sfx + "@edu.in", "Event User 2", Role.STUDENT), "Pass1234");

        // u1 registers successfully
        assertTrue(eventOps.registerForEvent(savedEvent.getId(), u1.getId()));

        // u1 tries to register again -> DuplicateMembershipException
        assertThrows(DuplicateMembershipException.class, () -> eventOps.registerForEvent(savedEvent.getId(), u1.getId()));

        // u2 tries to register -> EventFullException
        assertThrows(EventFullException.class, () -> eventOps.registerForEvent(savedEvent.getId(), u2.getId()));

        // Cancel registration succeeds
        assertTrue(eventOps.cancelEventRegistration(savedEvent.getId(), u1.getId()));

        // Now u2 can register
        assertTrue(eventOps.registerForEvent(savedEvent.getId(), u2.getId()));
    }
}
