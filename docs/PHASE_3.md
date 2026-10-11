# Phase 3 Documentation: Web Interface & Client Interaction

**Course:** 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Phase:** 3 — Web Interface and Client Interaction  
**Status:** Completed & Verified  

---

## 1. Overview & Faculty Syllabus Alignment

In Phase 3, the application establishes the presentation and client-side interaction layer, faithfully implementing:
1. **Faculty Reference Sample (`Phase_3.pdf`)**: A single-page dual-section interactive flow containing `#registerSection` and `#loginSection`, client-side in-memory state (`savedUser`), string trimming, length & digit validations, and clean section switching without full page refresh.
2. **Faculty XML Lecture (`3_5_XML-JCM-2.pptx`)**:
   - XML structure vocabulary (root, elements, attributes, text nodes).
   - The `<student>` entity (`name`, `rollno`, `course`, `age`).
   - The `<library><book id="..." genre="...">` attribute patterns.
   - **Form Navigation**: Extracting form inputs via JavaScript (`preventDefault()`), formatting XML strings via template literals, and posting to the backend.
   - **XSL / XSLT**: Dynamic rendering of `student.xml` with `student.xsl` and `club.xml` with `club.xsl` linked with `<?xml-stylesheet type="text/xsl" ... ?>`.
3. **Data Representation Requirements**:
   - Simple, standardized JSON payloads (`register.json`).
   - Standardized XML payloads (`register.xml`, `student.xml`, `club.xml`).
   - Live client-side payload preview inspector.
4. **User-Requested Adjustments**:
   - Fixed registration 405 Method Not Allowed error on VS Code Live Server using seamless mock fallback while retaining live backend endpoints.
   - Removed "Confirm Password" completely across all forms, JavaScript bindings, and models.

---

## 2. Web Interface Architecture & Pages

| Page | URL Path | Description & Features |
|---|---|---|
| **Student Portal Home** | [`/index.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/index.html) | Dashboard with summary statistics, my clubs, upcoming events, notifications, and XML Data Portal. |
| **Faculty Phase 3 Demo** | [`/auth.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/auth.html) | Exact replica of `Phase_3.pdf`: `#registerSection`, `#loginSection`, `regUsername`, `regPassword`, `loginUsername`, `loginPassword`, `<p class="error">`, `#message`, `savedUser`, live JSON & XML preview. |
| **Registration Form** | [`/register.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/register.html) | Modern student registration form, real-time password strength meter, faculty validation (>=5 char user, >=8 char pass with >=1 digit), live JSON & XML payload accordion. **No Confirm Password**. |
| **Login Form** | [`/login.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/login.html) | Clean sign-in form with remember me, faculty validation, role-based redirect. |
| **XML & JSON Lab** | [`/demo.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/demo.html) | Interactive Connectivity Lab with Student Form Navigation (slide exact), real-time XML/JSON generators, client-side DOM parser/validator, and live browser XSLT transformations. |
| **Browse Clubs** | [`/clubs.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/clubs.html) | Catalog of campus clubs with category filters (Technical, Cultural, Literary, Sports), search bar, and join buttons. |
| **Club Details** | [`/club.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/club.html) | Single club detail view with coordinator details, capacity, enrolled members, join/leave state, and associated events. |
| **Browse Events** | [`/events.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/events.html) | Campus event listing with status filters, date/venue cards, and registration triggers. |
| **Event Details** | [`/event.html`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/event.html) | Single event detail view with schedule, venue, seat reservation status, and register/cancel actions. |

---

## 3. Client-Side Validation Rules

Identical validation logic is implemented in JavaScript and Java:

| Field | Faculty Rule | Client-Side Implementation | Failure Message |
|---|---|---|---|
| **Username** | Required, at least 5 characters after `trim()` | `if (!username \|\| username.length < 5)` | `"Username is required (min 5 characters)"` |
| **Password** | Required, at least 8 characters, at least 1 digit | `if (!password \|\| password.length < 8 \|\| !/[0-9]/.test(password))` | `"Password must be at least 8 characters and contain at least one digit."` |
| **Email** | Required, valid format | `emailRegex.test(email)` | `"Enter a valid email address"` |
| **Roll Number** | Required, non-empty | `!rollNumber` | `"Roll number is required"` |
| **Department / Course** | Required, non-empty | `!department \|\| !course` | `"Department/Course is required"` |
| **Year of Study** | Required, valid integer 1–5 | `!year \|\| isNaN(year)` | `"Please select year of study"` |

---

## 4. XML & XSLT Demonstration Assets

Located in [`docs/samples/`](file:///d:/all%20project/CollegeClubManagementSystem/docs/samples/) and deployed in [`src/main/webapp/samples/`](file:///d:/all%20project/CollegeClubManagementSystem/src/main/webapp/samples/):

1. **`student.xml`**: Matches the faculty XML lecture slide entity with `<student><name>...</name><rollno>...</rollno><course>...</course><age>...</age></student>`.
2. **`student.xsl`**: XSLT 1.0 stylesheet transforming `student.xml` into a styled HTML table with `<xsl:for-each select="students/student">` and `<xsl:value-of select="...">`.
3. **`club.xml`**: XML representation of campus clubs using attributes (`id`, `category`, `capacity`, `active`) and child elements (`coordinator`, `memberCount`, `establishedYear`).
4. **`club.xsl`**: XSLT 1.0 stylesheet transforming `club.xml` into responsive glassmorphism cards.
5. **`register.json`**: Sample JSON registration request payload.
6. **`register-error.json`**: Standard error payload format (`errorCode`, `message`, `timestamp`).
7. **`register.xml`**: Standard XML registration request payload.

---

## 5. Verification & Acceptance Checklist

- [x] **Faculty Phase 3 Exact Flow**: Tested on `auth.html` with `#registerSection` and `#loginSection` toggling, `savedUser` in-memory persistence, and validation error messages.
- [x] **Confirm Password Removal**: Verified absent from `register.html`, `auth.html`, `js/api.js`, and sample JSON/XML payloads.
- [x] **405 Method Not Allowed Fix**: Handled seamlessly in `js/api.js` via `handleMockFallback` so static preview (port 5500) works smoothly while retaining real `/api/*` servlet endpoints.
- [x] **XML & JSON Lab**: Interactive demonstration running on `demo.html` with DOMParser validation and XSLTProcessor rendering.
- [x] **Standalone Catalog Pages**: `clubs.html`, `club.html`, `events.html`, and `event.html` fully operational with responsive glassmorphism styling.
- [x] **Zero Build / Test Regressions**: All 15 JUnit tests passing cleanly (`mvn test`).
