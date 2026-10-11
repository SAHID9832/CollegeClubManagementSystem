package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Notification;
import com.collegeclub.model.Student;
import com.collegeclub.model.User;
import com.collegeclub.service.impl.ClubService;
import com.collegeclub.service.impl.EventService;
import com.collegeclub.service.impl.UserService;
import com.collegeclub.util.XmlUtil;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Student Profile & Data Controller (REST API & XML)
 * Mapped to /api/students/*
 * Handles personal dashboard data, club enrollments, event registrations,
 * notifications, and XML student profile export/import.
 */
@WebServlet(name = "StudentServlet", urlPatterns = {"/api/students", "/api/students/*"})
public class StudentServlet extends BaseApiServlet {

    private final UserService userService = new UserService();
    private final ClubService clubService = new ClubService();
    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getAuthenticatedUser(req);
        if (currentUser == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        try {
            if (pathInfo.equals("/me") || pathInfo.equals("") || pathInfo.equals("/")) {
                Student student = userService.getStudentByUserId(currentUser.getUserId());
                writeSuccess(resp, "Student profile", student);
                return;
            }

            if (pathInfo.equals("/me/clubs") || pathInfo.equals("/clubs")) {
                List<Club> clubs = clubService.getStudentClubs(currentUser.getUserId());
                writeSuccess(resp, "Student enrolled clubs", clubs);
                return;
            }

            if (pathInfo.equals("/me/events") || pathInfo.equals("/events")) {
                List<Event> events = eventService.getStudentEvents(currentUser.getUserId());
                writeSuccess(resp, "Student registered events", events);
                return;
            }

            if (pathInfo.equals("/me/notifications") || pathInfo.equals("/notifications")) {
                List<Notification> notifs = userService.getNotifications(currentUser.getUserId());
                writeSuccess(resp, "Student notifications", notifs);
                return;
            }

            if (pathInfo.equals("/xml")) {
                Student student = userService.getStudentByUserId(currentUser.getUserId());
                String xml = XmlUtil.studentToXml(student);
                resp.setContentType("application/xml;charset=UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(xml);
                out.flush();
                return;
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Endpoint not found");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getAuthenticatedUser(req);
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if (pathInfo.equals("/xml")) {
            // Import / Validate Student XML
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            try {
                Map<String, String> data = XmlUtil.parseFacultyStudentXml(sb.toString());
                writeSuccess(resp, "Student XML parsed and validated successfully", data);
            } catch (ClubSystemException e) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "XML_PARSE_ERROR", e.getMessage());
            }
            return;
        }

        if (currentUser == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in");
            return;
        }

        try {
            // POST /api/students/me (Update profile)
            if (pathInfo.equals("/me") || pathInfo.equals("") || pathInfo.equals("/")) {
                handleProfileUpdate(currentUser, req, resp);
                return;
            }

            // POST /api/students/me/notifications/read-all
            if (pathInfo.equals("/me/notifications/read-all") || pathInfo.equals("/notifications/read-all")) {
                userService.markAllNotificationsRead(currentUser.getUserId());
                writeSuccess(resp, "All notifications marked as read", null);
                return;
            }

            // POST /api/students/me/notifications/{id}/read
            if (pathInfo.startsWith("/me/notifications/") || pathInfo.startsWith("/notifications/")) {
                String[] parts = pathInfo.split("/");
                for (int i = 0; i < parts.length; i++) {
                    if ("read".equalsIgnoreCase(parts[i]) && i > 0) {
                        try {
                            int notifId = Integer.parseInt(parts[i - 1]);
                            userService.markNotificationRead(notifId, currentUser.getUserId());
                            writeSuccess(resp, "Notification marked read", null);
                            return;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not supported");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = getAuthenticatedUser(req);
        if (currentUser == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/me") || pathInfo.equals("") || pathInfo.equals("/")) {
            handleProfileUpdate(currentUser, req, resp);
        } else {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Endpoint not found");
        }
    }

    private void handleProfileUpdate(User currentUser, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject json = readJsonBody(req, JsonObject.class);
        String fullName = json != null && json.has("fullName") ? json.get("fullName").getAsString() : currentUser.getFullName();
        String email = json != null && json.has("email") ? json.get("email").getAsString() : currentUser.getEmail();
        String department = json != null && json.has("department") ? json.get("department").getAsString() : "Computer Applications";
        String course = json != null && json.has("course") ? json.get("course").getAsString() : "MCA";
        int year = json != null && json.has("year") ? json.get("year").getAsInt() : 1;

        try {
            userService.updateStudentProfile(currentUser.getUserId(), fullName, email, department, course, year);
            Student updated = userService.getStudentByUserId(currentUser.getUserId());
            writeSuccess(resp, "Profile updated successfully", updated);
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
