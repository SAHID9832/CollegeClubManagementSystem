# PROJECT_STATE.md - College Club Management System

## Current Status
- **Current Phase**: Phase 1 Complete (Tagged `phase-1-complete`) -> Ready for Phase 2
- **Course**: 26MCA1L211 - Java and Web Programming Practical (MCA Semester I)
- **Active Git Branch**: main (committed and tagged `phase-1-complete`)
- **Last Verified Command**: `mvn test` (7/7 tests passed), `mvn exec:java` live acceptance runs

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
- [ ] **Phase 2: Structured Programming and Reliability** (Tag: `phase-2-complete`)
  - [ ] Interfaces in `com.collegeclub.service`: `UserOperations`, `ClubOperations`, `EventOperations`
  - [ ] Implementations in `com.collegeclub.service.impl`: `UserService`, `ClubService`, `EventService`
  - [ ] Exception hierarchy in `com.collegeclub.exception`: `ClubSystemException`, `InvalidInputException`, `InvalidUsernameException`, etc.
  - [ ] PBKDF2WithHmacSHA256 password hashing with salt in `com.collegeclub.util.PasswordUtil`
  - [ ] Central DAO layer & H2/MySQL DDL `schema.sql`
  - [ ] Preserved Phase 2 console application in `com.collegeclub.app.Main`
  - [ ] Unit tests for service operations, exceptions, PBKDF2 hashing
- [ ] **Phase 3: Web Interface and Client Interaction** (Tag: `phase-3-complete`)
  - [ ] Faculty Phase 3 sample recreated faithfully: `#registerSection`, `#loginSection`, `#showLogin`, `#showRegister`, `regUsername`, `regPassword`, `loginUsername`, `loginPassword`
  - [ ] Upgraded glassmorphism UI pages: `index.html`, `register.html`, `login.html`, `clubs.html`, `club.html`, `events.html`, `event.html`, `demo.html`
  - [ ] Live JSON preview (`{ "username": ..., "email": ... }` with masked password)
  - [ ] Live XML preview with `escapeXml()` utility
  - [ ] Faculty XML + XSLT demonstration files in `docs/samples/` (`student.xml` + `student.xsl`, `club.xml` + `club.xsl`)
  - [ ] Connectivity Lab (`demo.html`) with student form and payload previews
  - [ ] Frontend config flag `BACKEND_ENABLED = false` for in-memory demonstration
- [ ] **Phase 4: Server-Side Integration and Execution** (Tag: `phase-4-complete`)
  - [ ] `BACKEND_ENABLED = true` via `js/api.js`
  - [ ] Servlets: `RegisterServlet`, `LoginServlet`, `LogoutServlet`, `ClubServlet`, `EventServlet`, `MembershipServlet`, `EventRegistrationServlet`, `StatsServlet`, `AdminServlet`, `XmlServlet`, `HealthServlet`, `BaseApiServlet`
  - [ ] `HttpSession` tracking with `request.changeSessionId()`
  - [ ] `AuthFilter`, `CsrfFilter`, `SecurityHeadersFilter`, `EncodingFilter`
  - [ ] JSP pages with JSTL `<c:out>`: `dashboard.jsp`, `admin.jsp`, `profile.jsp`
  - [ ] Secure XML parsing (`disallow-doctype-decl`, disabled DTDs/external entities)
  - [ ] Automated smoke test script `scripts/smoke-test.ps1`
  - [ ] Deployment to Tomcat & full verification
- [ ] **Deliverables & Documentation**
  - [ ] `README.md`
  - [ ] `docs/PHASE_1.md`, `docs/PHASE_2.md`, `docs/PHASE_3.md`, `docs/PHASE_4.md`
  - [ ] `docs/API.md`, `docs/VIVA_QA.md` (25+ Q&A), `docs/DEMO_SCRIPT.md`

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
