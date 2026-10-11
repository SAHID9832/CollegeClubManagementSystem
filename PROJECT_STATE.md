# PROJECT_STATE.md - College Club Management System

## Current Status
- **Current Phase**: All Phases Complete (Phases 1, 2, 3, 4 Complete & Tagged)
- **Course**: 26MCA1L211 - Java and Web Programming Practical (MCA Semester I)
- **Active Git Branch**: main (committed and tagged `phase-4-complete`)
- **Last Verified Command**: `mvn test` (21/21 tests passed), `mvn package` (WAR built), `dist/collegeclub.war` generated

---

## Environment Detection Summary (Step 0)
- **Operating System**: Windows 11 (amd64)
- **JDK Version**: Oracle JDK 21.0.12 LTS (`java`, `javac` on PATH)
- **Maven Version**: Apache Maven 3.10.0 (`mvn` on PATH)
- **Git Version**: 2.51.0.windows.1
- **Apache Tomcat**: Not found in `%CATALINA_HOME%` or `C:\Program Files\Apache Software Foundation\`.
  - **Decision**: Target Tomcat 10.1 (`jakarta.servlet.*`, Servlet 6.0, JSP 3.1, JSTL 3.0), compatible with Java 17/21.
  - Development execution configured via Cargo Maven Plugin (`cargo:run`) downloading/running Tomcat 10.1 embedded/standalone.
  - Dedicated deployment and runner PowerShell scripts provided in `scripts/`.
- **Database**:
  - Local MySQL service `MYSQL80` is detected running.
  - Default embedded mode: H2 file-based DB at `${user.home}/collegeclub-data/collegeclub` for zero-configuration, 100% out-of-the-box reliability without needing local DB setup.
  - Pluggable MySQL support: full JDBC DAO layer swappable via `application.properties` and environment variables (`CLUB_DB_URL`, `CLUB_DB_USER`, `CLUB_DB_PASSWORD`).
- **Security Flaws Identified in Previous Backups**:
  - `_backup_initial/from_existing_folder/src/service/UserService.java` stored and queried passwords in plaintext SQL.
  - Hardcoded DB credentials (`root`/`root`).
  - JDK `HttpServer` used instead of Servlets/JSP/Tomcat container.
  - Hardcoded absolute URLs (`fetch("/login")`) breaking Tomcat context paths.
  - No XXE protection on XML parsing.
  - No CSRF token mechanism.

---

## Phase Execution Checklist
- [x] **Step 0: Discovery & Environment Detection**
- [x] **Phase 1: Core Application Setup and Design** (Tag: `phase-1-complete`)
  - [x] Faculty Phase 1 console classes in `com.collegeclub.phase1`: `User`, `Validation`, `Main`
  - [x] Production models in `com.collegeclub.model`: `User`, `Student`, `Admin`, `Club`, `Event`, `Role`
  - [x] Unit tests for string validation and model encapsulation (7/7 tests passed)
- [x] **Phase 2: Structured Programming and Reliability** (Tag: `phase-2-complete`)
  - [x] Interfaces in `com.collegeclub.service`: `UserOperations`, `ClubOperations`, `EventOperations`
  - [x] Implementations in `com.collegeclub.service.impl`: `UserService`, `ClubService`, `EventService`
  - [x] Exception hierarchy in `com.collegeclub.exception`: `ClubSystemException`, `InvalidInputException`, `InvalidUsernameException`, etc.
  - [x] PBKDF2WithHmacSHA256 password hashing with salt in `com.collegeclub.util.PasswordUtil`
  - [x] Central DAO layer & H2/MySQL DDL `schema.sql`
  - [x] Preserved Phase 2 console application in `com.collegeclub.app.Main`
  - [x] Unit tests for service operations, exceptions, PBKDF2 hashing (15/15 tests passed)
- [x] **Phase 3: Web Interface and Client Interaction** (Tag: `phase-3-complete`)
  - [x] Faculty Phase 3 sample recreated faithfully: `#registerSection`, `#loginSection`, `#showLogin`, `#showRegister`, `regUsername`, `regPassword`, `loginUsername`, `loginPassword`, `savedUser` in `auth.html`
  - [x] Upgraded glassmorphism UI pages: `index.html`, `register.html`, `login.html`, `clubs.html`, `club.html`, `events.html`, `event.html`, `auth.html`, `demo.html`
  - [x] Live JSON preview (`{ "username": ..., "email": ... }` with masked password) in `register.html` and `auth.html`
  - [x] Live XML preview with `escapeXml()` utility
  - [x] Faculty XML + XSLT demonstration files in `docs/samples/` and `src/main/webapp/samples/` (`student.xml` + `student.xsl`, `club.xml` + `club.xsl`)
  - [x] Connectivity Lab (`demo.html`) with student form, DOMParser validation, and live XSLTProcessor rendering
  - [x] Fixed registration 405 error on Live Server via `handleMockFallback` in `js/api.js`
  - [x] Completely removed Confirm Password field as requested
- [x] **Phase 4: Server-Side Integration and Execution** (Tag: `phase-4-complete`)
  - [x] Servlets: `RegisterServlet`, `LoginServlet`, `LogoutServlet`, `ClubServlet`, `EventServlet`, `StudentServlet`, `AdminServlet`, `XmlServlet`, `HealthServlet`, `BaseApiServlet`
  - [x] `HttpSession` tracking with `request.changeSessionId()` session fixation defense
  - [x] Filters: `AuthFilter`, `CsrfFilter`, `SecurityHeadersFilter`, `EncodingFilter`
  - [x] JSP pages with JSTL `<c:out>`: `dashboard.jsp`, `admin.jsp`, `profile.jsp`
  - [x] Secure XML processing via `XmlUtil` with XXE protection (`disallow-doctype-decl`)
  - [x] Automated smoke test script `scripts/smoke-test.ps1`
  - [x] Deployment script `scripts/deploy.ps1` (built `dist/collegeclub.war`)
  - [x] Tomcat 10.1 development runner `scripts/run-tomcat.ps1` via Cargo
- [x] **Runtime Hardening & Expansion Upgrades**:
  - [x] Resolved `clubs.forEach is not a function` & `notifs.forEach is not a function` via route precedence and `Array.isArray()` unwrapping
  - [x] Expanded database to 30 diverse college clubs (20 syllabus/dept clubs + 10 innovation clubs) with objectives, activities, icons, capacities, and faculty coordinators
  - [x] Seeded 8 realistic scheduled university events with future dates
  - [x] Replaced fake statistics (`totalUsers * 2`) with real DB metrics via `eventDao.countTotalRegistrations()` and `clubDao.countTotalMemberships()`
  - [x] Added persistent student profile fields (`rollNumber`, `department`, `course`, `year`) and in-app notifications (`notifications` table & DAO)
  - [x] Removed Confirm New Password across all forms and API endpoints
  - [x] Added dynamic Tomcat context path detection in `API_BASE` (`js/api.js`)
- [x] **Deliverables & Documentation**
  - [x] `README.md`
  - [x] `docs/PHASE_1.md`, `docs/PHASE_2.md`, `docs/PHASE_3.md`, `docs/PHASE_4.md`
  - [x] `docs/API.md`, `docs/VIVA_QA.md` (25+ Q&A), `docs/DEMO_SCRIPT.md`

---

## Working Commands Reference
```powershell
# Compile & run unit tests
mvn clean test

# Package WAR
mvn clean package

# Run development server via Cargo plugin (Tomcat 10.1)
mvn cargo:run

# Run Phase 1 standalone console
mvn test-compile exec:java -Dexec.mainClass="com.collegeclub.phase1.Main"

# Run Phase 2 standalone console
mvn test-compile exec:java -Dexec.mainClass="com.collegeclub.app.Main"
```
