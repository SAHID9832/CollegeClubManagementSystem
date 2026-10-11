# College Club Management System - API Specification

**Base URL (Tomcat):** `http://localhost:8080/collegeclub`  
**Authentication:** Cookie-based `JSESSIONID` or Bearer `X-CSRF-Token`  

---

## 1. Authentication Endpoints

### 1.1 Register Student
- **Endpoint:** `POST /api/auth/register` (also `/register`)
- **Headers:** `Content-Type: application/json`
- **Request Body (NO Confirm Password):**
```json
{
  "username": "sheeja",
  "password": "Password@123",
  "email": "sheeja@college.edu",
  "fullName": "Sheeja Mathew",
  "rollNumber": "26MCA012",
  "department": "Computer Applications",
  "course": "MCA",
  "year": 1
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Registration successful!",
  "data": {
    "userId": 105,
    "username": "sheeja",
    "email": "sheeja@college.edu",
    "fullName": "Sheeja Mathew",
    "role": "STUDENT"
  }
}
```
- **Error Response (400 Bad Request):**
```json
{
  "success": false,
  "errorCode": "InvalidPasswordException",
  "message": "Password must be at least 8 characters long and contain at least one digit.",
  "timestamp": "2026-10-12T01:00:00Z"
}
```

---

### 1.2 User Login
- **Endpoint:** `POST /api/auth/login` (also `/login`)
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "username": "sheeja",
  "password": "Password@123"
}
```
- **Success Response (200 OK):** Sets `Set-Cookie: JSESSIONID=...; HttpOnly; Path=/collegeclub`
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "4f8a3c21-...",
    "sessionId": "A3B12D...",
    "userId": 105,
    "username": "sheeja",
    "fullName": "Sheeja Mathew",
    "role": "STUDENT",
    "csrfToken": "4f8a3c21-..."
  }
}
```

---

### 1.3 User Logout
- **Endpoint:** `POST /api/auth/logout` (also `GET /logout`)
- **Success Response (200 OK):** Invalidates session and clears credentials.
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

## 2. Clubs Endpoints

### 2.1 List All Clubs
- **Endpoint:** `GET /api/clubs`
- **Query Parameters:**
  - `category` (optional, e.g., `Technical`, `Cultural`, `Sports`)
  - `search` (optional search keyword)
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Clubs retrieved",
  "data": [
    {
      "id": 1,
      "name": "Coding Club",
      "description": "Competitive programming, algorithms, and web development.",
      "category": "Technical",
      "coordinator": "Dr. Arul",
      "capacity": 60,
      "memberCount": 42,
      "active": true
    }
  ]
}
```

---

### 2.2 Get Club Details
- **Endpoint:** `GET /api/clubs/{id}`
- **Success Response (200 OK):** Returns single club object with coordinator and capacity info.

---

### 2.3 Join Club
- **Endpoint:** `POST /api/clubs/{id}/join`
- **Headers:** `X-CSRF-Token: <token>`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Successfully joined club!"
}
```

---

### 2.4 Leave Club
- **Endpoint:** `POST /api/clubs/{id}/leave`
- **Headers:** `X-CSRF-Token: <token>`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Successfully left club."
}
```

---

## 3. Events Endpoints

### 3.1 List Events
- **Endpoint:** `GET /api/events`
- **Query Parameters:** `clubId` (optional), `status` (optional, `OPEN`)
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Events retrieved",
  "data": [
    {
      "id": 101,
      "clubId": 1,
      "clubName": "Coding Club",
      "title": "Annual Hackathon 2026",
      "description": "24-hour campus hackathon.",
      "eventDate": "2026-10-25 09:00",
      "venue": "Main Auditorium",
      "capacity": 100,
      "registeredCount": 45,
      "status": "OPEN"
    }
  ]
}
```

---

### 3.2 Register for Event
- **Endpoint:** `POST /api/events/{id}/register`
- **Headers:** `X-CSRF-Token: <token>`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Successfully registered for event!"
}
```

---

### 3.3 Cancel Event Registration
- **Endpoint:** `DELETE /api/events/{id}/register` (or `POST /api/events/{id}/cancel`)
- **Headers:** `X-CSRF-Token: <token>`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Event registration cancelled successfully."
}
```

---

## 4. XML Connectivity Endpoints

### 4.1 Process Faculty Student Form XML
- **Endpoint:** `POST /api/xml/student`
- **Headers:** `Content-Type: application/xml`
- **Request Body (Slide Exact):**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<student>
  <name>Arun Kumar</name>
  <rollno>26MCA001</rollno>
  <course>MCA</course>
  <age>22</age>
</student>
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "XML student payload parsed successfully by Jakarta Servlet",
  "data": {
    "name": "Arun Kumar",
    "rollno": "26MCA001",
    "course": "MCA",
    "age": 22
  }
}
```

---

### 4.2 Export Clubs Catalog in XML
- **Endpoint:** `GET /api/xml/clubs`
- **Response Format:** `application/xml;charset=UTF-8`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<clubs>
  <club active="true" capacity="60" category="Technical" id="1">
    <name>Coding Club</name>
    <description>Competitive programming, algorithms, and web development.</description>
    <coordinator>
      <name>Dr. Arul</name>
      <email>arul@college.edu</email>
    </coordinator>
    <memberCount>42</memberCount>
  </club>
</clubs>
```

---

## 5. System Health & Diagnostics

### 5.1 Health Check
- **Endpoint:** `GET /api/health`
- **Success Response (200 OK):**
```json
{
  "status": "UP",
  "service": "College Club Management System",
  "version": "1.0.0",
  "timestamp": "2026-10-12T01:00:00Z",
  "database": "CONNECTED"
}
```
