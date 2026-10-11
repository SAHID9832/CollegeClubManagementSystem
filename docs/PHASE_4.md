# Phase 4 Documentation: Server-Side Integration, Execution & XML Connectivity

**Course:** 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Phase:** 4 — Server-Side Integration and Execution  
**Status:** Completed & Verified  

---

## 1. Overview & Architecture

Phase 4 integrates the web tier with the enterprise business logic and database persistence layers on Apache Tomcat 10.1 (`jakarta.servlet.*`), delivering:
1. **Jakarta Servlets**:
   - [`RegisterServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/RegisterServlet.java): Dual endpoint (`/register`, `/api/auth/register`) handling JSON & Form registration without confirm password.
   - [`LoginServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/LoginServlet.java): Dual endpoint (`/login`, `/api/auth/login`) establishing `HttpSession`, session fixation defense (`changeSessionId()`), and generating CSRF token.
   - [`LogoutServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/LogoutServlet.java): Invalidates session (`session.invalidate()`) and clears auth cookies.
   - [`ClubServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/ClubServlet.java): REST API (`/api/clubs/*`) for club discovery, creation, joining, and leaving.
   - [`EventServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/EventServlet.java): REST API (`/api/events/*`) for event catalog, scheduling, and registration.
   - [`StudentServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/StudentServlet.java): Profile data, enrollments, and XML profile export/import.
   - [`AdminServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/AdminServlet.java): Platform metrics, user role management, and CSV report downloads.
   - [`XmlServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/XmlServlet.java): Faculty slide Form Navigation endpoint (`/api/xml/student`) and live XML clubs catalog (`/api/xml/clubs`).
   - [`HealthServlet`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/controller/HealthServlet.java): Health check endpoint verifying uptime and DB connectivity.
2. **Servlet Filters**:
   - [`EncodingFilter`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/filter/EncodingFilter.java): UTF-8 character encoding on all requests and responses.
   - [`SecurityHeadersFilter`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/filter/SecurityHeadersFilter.java): Nosniff, SAMEORIGIN, XSS protection, and no-store headers.
   - [`AuthFilter`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/filter/AuthFilter.java): Guarding `/dashboard.jsp`, `/admin.jsp`, `/profile.jsp` against unauthenticated access.
   - [`CsrfFilter`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/filter/CsrfFilter.java): Validates `X-CSRF-Token` on state-changing API operations.
3. **Server-Rendered JSP Pages (JSTL 3.0 `<c:out>`)**:
   - [`dashboard.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/dashboard.jsp): Student dashboard displaying enrolled clubs, upcoming events, and session ID.
   - [`admin.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/admin.jsp): Administrator dashboard rendering user table and metrics with JSTL `<c:forEach>`.
   - [`profile.jsp`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/profile.jsp): Student academic profile with XML export.
4. **Secure XML Processing**:
   - [`XmlUtil`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/java/com/collegeclub/util/XmlUtil.java): Hardened W3C DOM parser with XXE vulnerabilities disabled (`disallow-doctype-decl = true`).

---

## 2. Servlet & URL Mapping Table

| Servlet Class | URL Pattern(s) | HTTP Methods | Responsibility |
|---|---|---|---|
| `RegisterServlet` | `/register`, `/api/auth/register` | `GET`, `POST` | Processes registration, verifies server-side rules (no confirm password), returns JSON or redirects. |
| `LoginServlet` | `/login`, `/api/auth/login` | `GET`, `POST` | Authenticates via PBKDF2 hash, establishes session, defends against session fixation. |
| `LogoutServlet` | `/logout`, `/api/auth/logout` | `GET`, `POST` | Invalidates `HttpSession`, clears authentication state. |
| `ClubServlet` | `/api/clubs`, `/api/clubs/*` | `GET`, `POST` | Browses clubs, filters by category, handles join/leave actions, and member lists. |
| `EventServlet` | `/api/events`, `/api/events/*` | `GET`, `POST`, `DELETE` | Lists events, registers student for event, cancels registration. |
| `StudentServlet` | `/api/students`, `/api/students/*` | `GET`, `POST` | Current user profile, student clubs/events, XML profile export and import. |
| `AdminServlet` | `/api/admin`, `/api/admin/*` | `GET`, `POST`, `PUT` | System stats, user role management, activity report in CSV. |
| `XmlServlet` | `/api/xml`, `/api/xml/*` | `GET`, `POST` | Faculty Form Navigation student XML receiver, clubs XML generator. |
| `HealthServlet` | `/api/health` | `GET` | Health check verifying status `UP` and database `CONNECTED`. |

---

## 3. Session Tracking & Security Architecture

1. **Session Lifecycle**:
   - `request.getSession(true)` invoked upon successful authentication.
   - `request.changeSessionId()` called immediately to prevent session fixation attacks.
   - Session timeout configured to 30 minutes in `WEB-INF/web.xml`.
   - Cookie configuration marked `http-only: true`.
2. **Access Control (`AuthFilter`)**:
   - Inspects `session.getAttribute("user")`.
   - If missing, captures current URL as `returnUrl` and redirects to `/login.html`.
   - Restricts `/admin.jsp` strictly to users with `Role.ADMIN`.
3. **Cross-Site Scripting (XSS) Mitigation**:
   - All JSP dynamic expressions rendered through `<c:out value="${...}" />` which automatically escapes HTML entities (`<`, `>`, `&`, `"`, `'`).
4. **XXE Injection Protection**:
   - XML DocumentBuilder explicitly disables inline DOCTYPE declarations:
     `dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)`
   - External entities and external DTDs are explicitly disabled.

---

## 4. Verification & Testing Evidence

All 21 unit and integration tests passing:
- `com.collegeclub.phase1.Phase1Test`: 7/7 tests passed.
- `com.collegeclub.phase2.Phase2Test`: 8/8 tests passed.
- `com.collegeclub.phase4.Phase4Test`: 6/6 tests passed.
- Total: 21 tests, 0 failures, 0 errors.
- Package output: `dist/collegeclub.war` (10.5 MB).
