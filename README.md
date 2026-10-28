# College Club Management System

**Course:** 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Platform:** Java Servlets + JSP + HttpSession on Apache Tomcat 10.1 (`jakarta.servlet.*`)  
**Build Tool:** Apache Maven (WAR Packaging)  
**Security:** PBKDF2 Password Hashing (600,000 iterations), CSRF Protection, XXE Defense  
**Data Formats:** JSON (Google Gson) & XML (W3C DOM Parser + XSLT Transformations)  

---

## 1. Project Overview

The **College Club Management System** is a complete, enterprise-grade web application developed phase-by-phase following the university syllabus and faculty reference curriculum. It expands upon the faculty's foundational "User Registration and Login System" into a full-featured campus organization portal where students discover clubs, manage memberships, and enroll in campus events, while faculty coordinators and administrators supervise activities.

### The Four Phases (Syllabus Roadmap)
- **Phase 1 — Core Application Setup and Design (Tagged `phase-1-complete`):** User entity with encapsulation, constructors, getters/setters, and Java string-handling validation. Exact faculty lab classes preserved in `com.collegeclub.phase1`.
- **Phase 2 — Structured Programming and Reliability (Tagged `phase-2-complete`):** Modular packages (`model`, `service`, `dao`, `exception`, `util`), `UserOperations` interface, custom exception hierarchy rooted at `ClubSystemException`, PBKDF2WithHmacSHA256 password hashing with salt, and JDBC persistence using `PreparedStatement`.
- **Phase 3 — Web Interface and Client Interaction (Tagged `phase-3-complete`):** Premium glassmorphism UI, client-side validation, faithful reproduction of faculty dual-section flow from `Phase_3.pdf` (`auth.html`), real-time JSON and XML payload generation, and XML+XSLT demonstration files (`student.xml` + `student.xsl`, `club.xml` + `club.xsl`).
- **Phase 4 — Server-Side Integration and Execution (Tagged `phase-4-complete`):** Server-side Jakarta Servlets (`RegisterServlet`, `LoginServlet`, `LogoutServlet`, `ClubServlet`, `EventServlet`, `StudentServlet`, `AdminServlet`, `XmlServlet`, `HealthServlet`), `HttpSession` lifecycle tracking with session fixation defense (`changeSessionId()`), JSP dashboards rendered via JSTL 3.0 `<c:out>`, servlet security filters, and secure XXE-hardened XML processing.

---

## 2. Default Test Credentials

| Role | Username | Password | Purpose |
|---|---|---|---|
| **System Administrator** | `admin` | `Admin@123` | Full administrative control, user role management, system metrics. |
| **Faculty Coordinator** | `coord01` | `Coord@123` | Club and event coordinator capabilities. |
| **Enrolled Student** | `student01` | `Student@123` | Sample student with enrolled clubs and event registrations. |
| **New Student** | *(Self-register)* | *(Min 8 chars, 1 digit)* | Live registration test without confirm password. |

---

## 3. Quick Start & Execution Commands

### Prerequisites
- Oracle JDK 17 or JDK 21 LTS (`java -version`, `javac -version`)
- Apache Maven 3.8+ (`mvn -v`)
- PowerShell (Windows) or Terminal

### 1. Compile & Run All Unit Tests
```powershell
mvn test
```
*Executes all 21 unit and integration tests across all four phases.*

### 2. Run Phase 1 Console Demonstration
```powershell
mvn exec:java -Dexec.mainClass="com.collegeclub.phase1.Main"
```

### 3. Run Phase 2 Console Demonstration
```powershell
mvn exec:java -Dexec.mainClass="com.collegeclub.app.Main"
```

### 4. Run Apache Tomcat 10.1 Development Server (Cargo)
```powershell
.\scripts\run-tomcat.ps1
# Or directly via Maven:
mvn cargo:run
```
Once started, open: **`http://localhost:8080/collegeclub/`**

### 5. Build Deployable WAR Package
```powershell
.\scripts\deploy.ps1
# Output: dist/collegeclub.war (10.5 MB deployable to any Tomcat 10.1+ server)
```

### 6. Run Automated Integration Smoke Tests
```powershell
powershell -ExecutionPolicy Bypass -File scripts\smoke-test.ps1
```

---

## 4. Key Web Pages & Endpoints

| Page / Endpoint | Purpose | Highlights |
|---|---|---|
| [`index.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/index.html) | Student Portal Home | Dashboard, quick stats, campus clubs, events, XML Data Portal. |
| [`auth.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/auth.html) | Faculty Phase 3 Flow | Exact replica of `Phase_3.pdf`: `#registerSection`, `#loginSection`, `savedUser`, live JSON & XML previews. |
| [`register.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/register.html) | Registration Page | Modern form, password strength meter, **no confirm password**, live JSON & XML payload accordion. |
| [`login.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/login.html) | Sign In Page | Clean sign-in with remember me and role-based redirect. |
| [`demo.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/demo.html) | XML & JSON Lab | Interactive test bench: Student Form Navigation (slide exact), DOMParser validation, live browser XSLT execution. |
| [`clubs.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/clubs.html) | Browse Clubs | Category filters (Technical, Cultural, Literary, Sports) and search bar. |
| [`club.html?id=1`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/club.html) | Club Details | Coordinator details, capacity, enrolled members, join/leave actions. |
| [`events.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/events.html) | Browse Events | Upcoming workshops, hackathons, and registration triggers. |
| [`event.html?id=101`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/event.html) | Event Details | Event schedule, venue, seat reservation status, register/cancel actions. |
| [`dashboard.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/dashboard.jsp) | Server-Side Dashboard | Rendered on Tomcat via `HttpSession` and JSTL 3.0 `<c:out>`. |
| [`admin.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/admin.jsp) | Administrator Dashboard | Protected by `AuthFilter` (ADMIN role), renders user directory and metrics. |
| [`profile.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/profile.jsp) | Student Profile | Academic credentials and XML profile export. |
| [`samples/student.xml`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/samples/student.xml) | Faculty XML Slide Sample | Linked with `student.xsl` for direct browser transformation. |
| [`samples/club.xml`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/samples/club.xml) | XML Club Catalog | Linked with `club.xsl` for responsive glassmorphism card rendering. |

---

## 5. Project Directory Structure

```
CollegeClubManagementSystem/
├── pom.xml                                  # Maven project configuration (WAR packaging, Java 17 release)
├── README.md                                # Master project documentation & presentation guide
├── scripts/
│   ├── smoke-test.ps1                       # Automated 9-step integration test suite
│   ├── deploy.ps1                           # Production WAR packaging and deployment script
│   └── run-tomcat.ps1                       # Cargo Tomcat 10.1 development server runner
├── docs/
│   ├── PHASE_1.md                           # Phase 1 documentation & verification
│   ├── PHASE_2.md                           # Phase 2 documentation & verification
│   ├── PHASE_3.md                           # Phase 3 documentation & verification
│   ├── PHASE_4.md                           # Phase 4 documentation & verification
│   ├── API.md                               # REST & XML API specification
│   ├── DEMO_SCRIPT.md                       # Viva presentation walkthrough script (25 marks)
│   ├── VIVA_QA.md                           # 25+ comprehensive Viva Voce questions & answers
│   └── samples/                             # XML and JSON reference samples (student.xml, club.xml, etc.)
└── src/
    ├── main/
    │   ├── java/com/collegeclub/
    │   │   ├── app/                         # CLI Console Entrypoint (Phase 1 & 2 Demo)
    │   │   ├── controller/                  # Jakarta Servlets (Login, Register, Club, Event, Admin, etc.)
    │   │   ├── dao/                         # DAO interfaces & PreparedStatement JDBC implementations
    │   │   ├── exception/                   # Exception hierarchy rooted at ClubSystemException
    │   │   ├── filter/                      # EncodingFilter, SecurityHeadersFilter, AuthFilter, CsrfFilter
    │   │   ├── listener/                    # UserSessionListener (HttpSessionListener for session monitoring)
    │   │   ├── model/                       # Domain Entities (User, Student, Club, Event, UserSessionInfo)
    │   │   ├── phase1/                      # Preserved faculty Phase 1 reference classes
    │   │   ├── service/                     # Service interfaces & business logic implementations
    │   │   └── util/                        # DBConnection, PasswordUtil, SessionRegistry, XmlUtil
    │   ├── resources/
    │   │   ├── application.properties       # Environment configuration (H2 / MySQL)
    │   │   └── schema.sql                   # Database schema (H2 & MySQL compatible)
    │   └── webapp/                          # Single canonical Web Application Root
    │       ├── WEB-INF/web.xml              # Deployment descriptor (Jakarta EE 10 / Servlet 6.0)
    │       ├── css/                         # Glassmorphism & CampusSphere CSS styles
    │       ├── js/api.js                    # Modular API client with session management & fallback
    │       ├── images/                      # Branding, campus hero & logo graphics
    │       ├── index.html                   # CampusSphere student portal home
    │       ├── login.html                   # Role-separated Sign In (Admin / Student)
    │       ├── register.html                # Student registration page
    │       ├── admin.html                   # Full Admin Portal with Live User Sessions Monitor
    │       ├── admin.jsp                    # Server-rendered admin dashboard (JSTL)
    │       ├── dashboard.jsp                # Server-rendered student dashboard (JSTL)
    │       ├── clubs.html                   # Browse & join campus clubs
    │       ├── events.html                  # Browse & register for campus events
    │       └── profile.jsp                  # Student academic profile
    └── test/java/com/collegeclub/
        ├── phase1/Phase1Test.java           # Phase 1 unit tests (7 tests)
        ├── phase2/Phase2Test.java           # Phase 2 unit tests (8 tests)
        └── phase4/Phase4Test.java           # Phase 4 integration & XXE tests (6 tests)
```

---

## 6. Architecture & Security Highlights

1. **Strictly No Confirm Password:** The confirm password field was completely removed across all forms, JavaScript bindings, controllers, and models to match user requirements.
2. **VS Code Live Server Support:** `js/api.js` automatically detects Live Server (port 5500) and provides graceful fallback so pages can be previewed without a running Java server while retaining full connectivity to Jakarta Servlets when running on Tomcat.
3. **Session Fixation Defense:** In `LoginServlet`, `request.changeSessionId()` is invoked upon authentication to guarantee unpredictable session tokens.
4. **XSS Prevention:** All server-rendered dynamic data in JSP uses JSTL 3.0 `<c:out value="${...}" />` to auto-escape HTML entities.
5. **XXE Protection:** All DOM parser instances in `XmlUtil` have `disallow-doctype-decl` enabled, external DTD loading disabled, and external general/parameter entities disabled.
6. **SQL Injection Immunity:** All database queries utilize parameterized `PreparedStatement` with zero string concatenation.

---

## 7. Active User Sessions & Session ID Monitoring (Teacher Requirement)

In compliance with syllabus and teacher evaluation requirements:
1. **Generation:** When any user (Admin, Student, Coordinator) logs in, the servlet container generates an `HttpSession` with a cryptographically secure `JSESSIONID`.
2. **Fixation Defense:** `request.changeSessionId()` is called immediately to defend against session hijacking.
3. **Tracking Architecture:**
   - **`UserSessionListener`**: An application-level `@WebListener` implementing `HttpSessionListener` catches container session creation and expiration events.
   - **`SessionRegistry`**: A thread-safe in-memory registry (`ConcurrentHashMap`) that maps each active Session ID to user credentials, IP address, login timestamp, and activity state.
4. **Admin Surveillance Panel:**
   - Admins can open **Active User Sessions** in the Admin Portal to view live Session IDs, authenticated usernames, roles, and IP addresses.
   - Admins can programmatically **terminate/invalidate** any user's session with one click, forcing an instant logout.
