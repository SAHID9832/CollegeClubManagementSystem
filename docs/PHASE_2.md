# Phase 2 — Structured Programming and Reliability

**Course**: 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Phase Status**: Completed & Verified  
**Git Tag**: `phase-2-complete`

---

## 1. Syllabus Mapping & Objectives

In accordance with the university syllabus for **Phase 2 (Structured Programming and Reliability)**:
- **Package Organization**: Modularize codebase into distinct layers: `model`, `service`, `service.impl`, `dao`, `dao.impl`, `exception`, `util`, and `app`.
- **Interface-Driven Design**: Define service contracts (`UserOperations`, `ClubOperations`, `EventOperations`) enforcing loose coupling.
- **Robust Exception Handling**: Create custom exception hierarchies rooted at `ClubSystemException` extending Java's `java.lang.Exception`.
- **Reliable Persistence Layer**: Implement JDBC Data Access Objects (DAOs) using `PreparedStatement` to eliminate SQL injection vulnerabilities.
- **Enterprise Authentication & Password Security**: Replace insecure plaintext/hardcoded credentials with OWASP-compliant `PBKDF2WithHmacSHA256` hashing and per-user cryptographic salts.

---

## 2. Package Architecture Diagram

```mermaid
flowchart TD
    subgraph UI_Web ["Presentation Layer (Servlets & Web)"]
        Controllers["com.collegeclub.controller.*"]
        Filters["com.collegeclub.filter.*"]
    end

    subgraph Service_Layer ["Service Layer (Business Logic & Contracts)"]
        UserOps["interface UserOperations"]
        ClubOps["interface ClubOperations"]
        EventOps["interface EventOperations"]
        UserService["class UserService"]
        ClubService["class ClubService"]
        EventService["class EventService"]
        UserOps <|.. UserService
        ClubOps <|.. ClubService
        EventOps <|.. EventService
    end

    subgraph DAO_Layer ["Persistence Layer (JDBC & SQL)"]
        UserDao["interface UserDao"]
        ClubDao["interface ClubDao"]
        MemDao["interface MembershipDao"]
        EventDao["interface EventDao"]
        RegDao["interface EventRegistrationDao"]
        UserDaoImpl["class UserDaoImpl"]
        ClubDaoImpl["class ClubDaoImpl"]
        MemDaoImpl["class MembershipDaoImpl"]
        EventDaoImpl["class EventDaoImpl"]
        RegDaoImpl["class EventRegistrationDaoImpl"]
        UserDao <|.. UserDaoImpl
        ClubDao <|.. ClubDaoImpl
        MemDao <|.. MemDaoImpl
        EventDao <|.. EventDaoImpl
        RegDao <|.. RegDaoImpl
    end

    subgraph Core_Utils ["Utilities & Exception Hierarchy"]
        Exceptions["com.collegeclub.exception.*\n(ClubSystemException, InvalidInputException...)"]
        Utils["com.collegeclub.util.*\n(PasswordUtil, Validation, DBConnection, ConfigLoader)"]
        Models["com.collegeclub.model.*\n(User, Student, Admin, Club, Event, Role)"]
    end

    Controllers --> Service_Layer
    Service_Layer --> DAO_Layer
    Service_Layer --> Core_Utils
    DAO_Layer --> Core_Utils
```

---

## 3. Exception Hierarchy & Status Mapping Table

All system exceptions inherit from [`ClubSystemException`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/exception/ClubSystemException.java), providing consistent error codes and HTTP response status hints:

| Exception Class | Parent Class | HTTP Status | Error Code | Description / Trigger Scenario |
|---|---|---|---|---|
| `ClubSystemException` | `java.lang.Exception` | 500 | `SYSTEM_ERROR` | Base exception for all checked system errors. |
| `InvalidInputException` | `ClubSystemException` | 400 | `INVALID_INPUT` | Faculty baseline exception for generic validation failures. |
| `InvalidUsernameException` | `InvalidInputException` | 400 | `INVALID_USERNAME` | Username empty, whitespace, or less than 5 characters. |
| `InvalidPasswordException` | `InvalidInputException` | 400 | `INVALID_PASSWORD` | Password less than 8 characters or missing required digit. |
| `DuplicateUserException` | `ClubSystemException` | 409 | `DUPLICATE_USER` | Username or email address already registered in database. |
| `AuthenticationException` | `ClubSystemException` | 401 | `AUTHENTICATION_FAILED` | Invalid login credentials or signature verification failure. |
| `UnauthorizedException` | `ClubSystemException` | 401 | `UNAUTHORIZED` | Request missing valid authentication session or token. |
| `ForbiddenException` | `ClubSystemException` | 403 | `FORBIDDEN` | Authenticated user lacks required role (e.g. non-admin access). |
| `ClubNotFoundException` | `ClubSystemException` | 404 | `CLUB_NOT_FOUND` | Requested club identifier does not exist. |
| `EventNotFoundException` | `ClubSystemException` | 404 | `EVENT_NOT_FOUND` | Requested event identifier does not exist. |
| `DuplicateMembershipException`| `ClubSystemException` | 409 | `DUPLICATE_MEMBERSHIP`| User attempting to join a club they already belong to. |
| `MembershipLimitReachedException`| `ClubSystemException`| 409 | `MEMBERSHIP_LIMIT_REACHED`| Club has reached its configured maximum capacity. |
| `InvalidEventRegistrationException`| `InvalidInputException`| 400 | `INVALID_EVENT_REGISTRATION`| Invalid parameters supplied for event registration. |
| `EventFullException` | `ClubSystemException` | 409 | `EVENT_FULL` | Event has reached maximum capacity of seats. |
| `InvalidRequestException` | `ClubSystemException` | 400 | `INVALID_REQUEST` | Malformed JSON/XML payload or missing mandatory headers. |

---

## 4. Why Hardcoded `admin/admin123` Was Replaced

In the faculty's initial prototype, authentication was hardcoded:
```java
// Prototype flaw:
if (username.equals("admin") && password.equals("admin123")) { return true; }
```
### Rationale for Architectural Replacement:
1. **Security Vulnerability**: Hardcoding credentials embeds secrets directly into compiled bytecode, easily decompiled with standard reverse-engineering tools.
2. **Immutability**: Statically hardcoded credentials prevent administrators from rotating or updating passwords.
3. **Multi-User Isolation**: Prevents storing individual student accounts and role differentiation.
4. **Production Architecture**: In Phase 2, the admin account is securely seeded into the database on first run using `ConfigLoader.getAdminPassword()` (defaulting to environment variable `CLUB_ADMIN_PASSWORD`). The password hash is stored in PBKDF2 format (`pbkdf2$<iterations>$<salt>$<hash>`). Subsequent logins query the database and verify the hash in constant time (`MessageDigest.isEqual`).

---

## 5. Password Security Architecture (`PasswordUtil`)

- **Algorithm**: `PBKDF2WithHmacSHA256`
- **Salt Generation**: 16 cryptographically secure random bytes generated via `java.security.SecureRandom`.
- **Iteration Count**: 600,000 iterations in production (configurable via `security.pbkdf2.iterations` or `CLUB_PBKDF2_ITERATIONS`). Fast iteration mode (1,000) supported for rapid unit tests.
- **Timing Attack Defense**: Comparisons use `MessageDigest.isEqual` to guarantee execution time is independent of candidate matching length.
- **Serialization Format**: `pbkdf2$<iterations>$<saltBase64>$<hashBase64>`.

---

## 6. Acceptance Test Results (15/15 Passed)

```text
[INFO] Running com.collegeclub.phase1.Phase1Test
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.collegeclub.phase2.Phase2Test
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Verified Test Cases:
1. `testPasswordHashAndVerify`: PBKDF2 format, verification, wrong password rejection.
2. `testFacultyValidateUserContract`: format checks through interface (`sheeja`/`pass1234` $\to$ true, `abc`/`pass1234` $\to$ false).
3. `testInvalidRegistrationCases`: short username, invalid email, short password, missing digit.
4. `testDuplicateUserException`: duplicate username and duplicate email detection.
5. `testLoginScenarios`: valid authentication, invalid password rejection, non-existent user handling.
6. `testClubMembershipOperations`: join club, duplicate membership rejection, leave club.
7. `testClubCapacityLimit`: max capacity enforcement (`MembershipLimitReachedException`).
8. `testEventRegistrationAndCapacity`: event registration, duplicate registration rejection, full capacity rejection (`EventFullException`), cancellation, re-registration.

---

## 7. Execution Commands

```powershell
# Run all unit tests (Phases 1 & 2)
mvn test

# Run Phase 2 Console Main program
mvn exec:java "-Dexec.mainClass=com.collegeclub.app.Main"
```
