# Practical Examination Demonstration Script (25 Marks Demo)

**Course:** 26MCA1L211 · Java and Web Programming Practical · MCA Semester I  
**Project:** College Club Management System  
**Audience:** Faculty Examiner / Viva Board  

---

## 1. Preparation Before Demo

1. Open PowerShell terminal in project root:
   ```powershell
   d:
   cd "d:\all project\CollegeClubManagementSystem"
   ```
2. Verify all test suites pass:
   ```powershell
   mvn test
   ```
   *Expected:* `Tests run: 21, Failures: 0, Errors: 0, BUILD SUCCESS`.

---

## 2. Step-by-Step Viva Presentation Walkthrough

### Step 1: Core Design & Phase 1 Foundation
- **What to say:**
  > *"Sir, following your instructions, Phase 1 establishes the User entity with encapsulation, constructors, getters, and string-handling validation in Java. We preserved the exact lab program classes in `com.collegeclub.phase1`."*
- **Action:**
  Run the Phase 1 console runner:
  ```powershell
  mvn exec:java -Dexec.mainClass="com.collegeclub.phase1.Main"
  ```
  Enter valid inputs (`sheeja` / `pass1234`) and invalid inputs to show the validation messages.

---

### Step 2: Reliability, Custom Exceptions & PBKDF2 Hashing (Phase 2)
- **What to say:**
  > *"In Phase 2, we modularized the project into packages `model`, `service`, `dao`, and `exception`. We implemented the `UserOperations` interface and created a custom exception hierarchy rooted at `ClubSystemException`. For security, passwords are never stored in plaintext—they are hashed using PBKDF2WithHmacSHA256 with a 16-byte cryptographically secure salt."*
- **Action:**
  Show `com.collegeclub.util.PasswordUtil` and run Phase 2 console application:
  ```powershell
  mvn exec:java -Dexec.mainClass="com.collegeclub.app.Main"
  ```
  Demonstrate registration, database persistence with `PreparedStatement`, and PBKDF2 hash verification on login.

---

### Step 3: Exact Faculty Phase 3 Flow (`auth.html`)
- **What to say:**
  > *"In Phase 3, we implemented your exact lab specification from `Phase_3.pdf`. This includes `#registerSection` and `#loginSection`, client-side state in `savedUser`, length and digit validation, and toggling without page reload. We also removed the confirm password field as instructed and added real-time JSON and XML payload previews."*
- **Action:**
  Open `auth.html` in browser (`http://localhost:5500/web/auth.html` or `http://localhost:8080/collegeclub/auth.html`).
  1. Type username `< 5` chars -> See `"Username must be at least 5 characters."` error.
  2. Type password without digit -> See `"Password must be at least 8 characters and contain at least one digit."` error.
  3. Enter valid inputs -> Click Register -> Section automatically switches to Login with success message in `#message`.
  4. Enter matching credentials -> See login success verified against in-memory `savedUser`.

---

### Step 4: XML & JSON Connectivity Lab (`demo.html`)
- **What to say:**
  > *"From your XML lecture slides (3_5_XML-JCM-2.pptx), we implemented Form Navigation for the `<student>` entity (`name`, `rollno`, `course`, `age`), dynamic XML template literals, DOMParser validation, and live browser XSLT transformations using `student.xml` + `student.xsl` and `club.xml` + `club.xsl`."*
- **Action:**
  Open `demo.html` in browser.
  1. Change student inputs -> Watch live XML and JSON change in real-time.
  2. Click **"Post to Server"** -> Shows HTTP POST to `/api/xml/student` and parsed server response.
  3. Click **"Validate XML"** -> Demonstrates well-formedness validation.
  4. Click **"Render student.xml + student.xsl"** and **"Render club.xml + club.xsl"** -> Shows native XSLT execution rendering raw XML into HTML view.

---

### Step 5: Server-Side Servlets, HttpSession & JSP Dashboards (Phase 4)
- **What to say:**
  > *"In Phase 4, we transitioned from client-side state to server-side Jakarta Servlets running on Apache Tomcat 10.1. We maintain authenticated state via `HttpSession` with session fixation defense (`changeSessionId()`), protect JSP pages using `AuthFilter`, prevent CSRF attacks with `CsrfFilter`, and render dashboards securely using JSTL `<c:out>`."*
- **Action:**
  1. Sign in via `login.html` (e.g., student account or admin `admin` / `Admin@123`).
  2. Access `dashboard.jsp` -> Show session ID, enrolled clubs, upcoming events.
  3. Try accessing `dashboard.jsp` in incognito or without login -> Shows automatic redirect to `login.html`.
  4. Log in as `admin` -> Access `admin.jsp` -> Show user table rendered via JSTL and CSV export.
  5. Click Log Out -> Demonstrates session invalidation via `LogoutServlet`.

---

### Step 6: Automated Smoke Test Verification
- **What to say:**
  > *"To ensure reliability and continuous integration, we have an automated PowerShell smoke test that exercises all 9 core server-side integration flows."*
- **Action:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File scripts/smoke-test.ps1
  ```
  *Expected:* All 9 integration steps pass.
