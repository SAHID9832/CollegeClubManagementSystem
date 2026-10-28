package com.collegeclub.controller;

import com.collegeclub.dao.impl.ClubDaoImpl;
import com.collegeclub.dao.impl.EventDaoImpl;
import com.collegeclub.dao.impl.EventRegistrationDaoImpl;
import com.collegeclub.dao.impl.MembershipDaoImpl;
import com.collegeclub.dao.impl.UserDaoImpl;
import com.collegeclub.model.Registration;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.util.SessionRegistry;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Administrative Operations Controller (REST API)
 * Mapped to /api/admin/*
 * Handles platform statistics, user management, participant management, role assignments, and CSV report export.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/api/admin", "/api/admin/*"})
public class AdminServlet extends BaseApiServlet {

    private final UserDaoImpl userDao = new UserDaoImpl();
    private final ClubDaoImpl clubDao = new ClubDaoImpl();
    private final EventDaoImpl eventDao = new EventDaoImpl();
    private final EventRegistrationDaoImpl registrationDao = new EventRegistrationDaoImpl();
    private final MembershipDaoImpl membershipDao = new MembershipDaoImpl();

    private boolean checkAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = getAuthenticatedUser(req);
        if (u == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
            return false;
        }
        if (u.getRole() != Role.ADMIN) {
            writeError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Administrative privilege required");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        // GET /api/admin/stats
        if (pathInfo.equals("/stats") || pathInfo.equals("") || pathInfo.equals("/")) {
            try {
                int totalStudents = userDao.countTotalStudents();
                int totalClubs = clubDao.getAllClubs().size();
                int totalEvents = eventDao.getAllEvents().size();
                int totalRegistrations = eventDao.countTotalRegistrations();
                int totalMemberships = clubDao.countTotalMemberships();
                int pendingApprovals = clubDao.countPendingMemberships();

                JsonObject stats = new JsonObject();
                stats.addProperty("totalStudents", totalStudents);
                stats.addProperty("activeClubs", totalClubs);
                stats.addProperty("upcomingEvents", totalEvents);
                stats.addProperty("totalRegistrations", totalRegistrations);
                stats.addProperty("totalMemberships", totalMemberships);
                stats.addProperty("pendingApprovals", pendingApprovals);
                stats.addProperty("activeSessions", SessionRegistry.getActiveCount());

                writeSuccess(resp, "Platform statistics", stats);
                return;
            } catch (java.sql.SQLException e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "Failed to retrieve statistics: " + e.getMessage());
                return;
            }
        }

        // GET /api/admin/sessions
        if (pathInfo.equals("/sessions")) {
            writeSuccess(resp, "Active user sessions", SessionRegistry.getAllSessions());
            return;
        }

        // GET /api/admin/users
        if (pathInfo.equals("/users")) {
            List<User> users = userDao.getAllUsers();
            writeSuccess(resp, "Users list", users);
            return;
        }

        // GET /api/admin/participants or /api/admin/registrations
        if (pathInfo.equals("/participants") || pathInfo.equals("/registrations")) {
            try {
                List<Registration> list = registrationDao.findAllRegistrationsDetailed();
                String eventIdParam = req.getParameter("eventId");
                String statusParam = req.getParameter("status");
                String searchParam = req.getParameter("search");

                if (eventIdParam != null && !eventIdParam.trim().isEmpty()) {
                    try {
                        int eventId = Integer.parseInt(eventIdParam.trim());
                        list.removeIf(r -> r.getEventId() != eventId);
                    } catch (NumberFormatException ignored) {}
                }
                if (statusParam != null && !statusParam.trim().isEmpty() && !statusParam.equalsIgnoreCase("ALL")) {
                    list.removeIf(r -> !statusParam.equalsIgnoreCase(r.getStatus()));
                }
                if (searchParam != null && !searchParam.trim().isEmpty()) {
                    String q = searchParam.trim().toLowerCase();
                    list.removeIf(r -> !(
                        (r.getStudentName() != null && r.getStudentName().toLowerCase().contains(q)) ||
                        (r.getUsername() != null && r.getUsername().toLowerCase().contains(q)) ||
                        (r.getEmail() != null && r.getEmail().toLowerCase().contains(q)) ||
                        (r.getStudentId() != null && r.getStudentId().toLowerCase().contains(q)) ||
                        (r.getEventTitle() != null && r.getEventTitle().toLowerCase().contains(q)) ||
                        (r.getClubName() != null && r.getClubName().toLowerCase().contains(q))
                    ));
                }

                writeSuccess(resp, "Event participants list", list);
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "Failed to retrieve participants: " + e.getMessage());
                return;
            }
        }

        // GET /api/admin/reports/csv
        if (pathInfo.equals("/reports/csv")) {
            resp.setContentType("text/csv;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"college_club_report.csv\"");
            PrintWriter out = resp.getWriter();
            out.println("User ID,Username,Email,Full Name,Role,Status");
            for (User u : userDao.getAllUsers()) {
                out.printf("%d,%s,%s,%s,%s,%s%n",
                        u.getUserId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getFullName() != null ? u.getFullName() : "",
                        u.getRole().name(),
                        u.isActive() ? "ACTIVE" : "INACTIVE"
                );
            }
            out.flush();
            return;
        }

        // GET /api/admin/reports/participants/csv
        if (pathInfo.equals("/reports/participants/csv")) {
            resp.setContentType("text/csv;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"event_participants_report.csv\"");
            PrintWriter out = resp.getWriter();
            out.println("Registration ID,Event Title,Club Name,Student Name,Username,Email,Roll Number,Department,Course,Registered At,Status");
            try {
                for (Registration r : registrationDao.findAllRegistrationsDetailed()) {
                    out.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            r.getId(),
                            r.getEventTitle() != null ? r.getEventTitle() : "",
                            r.getClubName() != null ? r.getClubName() : "",
                            r.getStudentName() != null ? r.getStudentName() : "",
                            r.getUsername() != null ? r.getUsername() : "",
                            r.getEmail() != null ? r.getEmail() : "",
                            r.getStudentId() != null ? r.getStudentId() : "",
                            r.getDepartment() != null ? r.getDepartment() : "",
                            r.getCourse() != null ? r.getCourse() : "",
                            r.getRegisteredAt() != null ? r.getRegisteredAt() : "",
                            r.getStatus() != null ? r.getStatus() : ""
                    );
                }
            } catch (Exception ignored) {}
            out.flush();
            return;
        }

        // GET /api/admin/clubs/{id}/members
        if (pathInfo.startsWith("/clubs/") && pathInfo.endsWith("/members")) {
            try {
                String[] parts = pathInfo.split("/");
                int clubId = Integer.parseInt(parts[2]);
                List<User> members = membershipDao.findMembersByClubId(clubId);
                writeSuccess(resp, "Club members list", members);
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                return;
            }
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Resource not found");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if (pathInfo.startsWith("/sessions/") && pathInfo.endsWith("/terminate")) {
            String targetSessionId = pathInfo.substring("/sessions/".length(), pathInfo.length() - "/terminate".length());
            boolean ok = SessionRegistry.terminateSession(targetSessionId);
            if (ok) {
                writeSuccess(resp, "Session terminated successfully", null);
            } else {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Session not found or already inactive");
            }
            return;
        }

        doPut(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        // PUT /api/admin/users/{id}/role
        if (pathInfo.contains("/users/")) {
            String[] parts = pathInfo.split("/");
            if (parts.length >= 4 && "role".equalsIgnoreCase(parts[3])) {
                try {
                    int userId = Integer.parseInt(parts[2]);
                    JsonObject json = readJsonBody(req, JsonObject.class);
                    if (json == null || !json.has("role")) {
                        writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "Role required");
                        return;
                    }
                    Role newRole = Role.valueOf(json.get("role").getAsString().toUpperCase());
                    boolean ok = userDao.updateUserRole(userId, newRole);
                    if (ok) {
                        writeSuccess(resp, "User role updated successfully", null);
                    } else {
                        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "User not found");
                    }
                    return;
                } catch (IllegalArgumentException e) {
                    writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ROLE", "Invalid role specified");
                    return;
                } catch (java.sql.SQLException e) {
                    writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                    return;
                }
            }

            // PUT /api/admin/users/{id}/status (Enable / Disable account authorization)
            if (parts.length >= 4 && "status".equalsIgnoreCase(parts[3])) {
                try {
                    int userId = Integer.parseInt(parts[2]);
                    JsonObject json = readJsonBody(req, JsonObject.class);
                    if (json == null || !json.has("active")) {
                        writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "active boolean property required");
                        return;
                    }
                    boolean active = json.get("active").getAsBoolean();
                    User currentAdmin = getAuthenticatedUser(req);
                    if (currentAdmin != null && currentAdmin.getId() == userId && !active) {
                        writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "CANNOT_DISABLE_SELF", "Admin cannot disable their own account");
                        return;
                    }

                    boolean ok = userDao.updateUserStatus(userId, active);
                    if (ok) {
                        writeSuccess(resp, active ? "User account enabled successfully" : "User account disabled successfully", null);
                    } else {
                        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "User not found");
                    }
                    return;
                } catch (Exception e) {
                    writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                    return;
                }
            }
        }

        // PUT /api/admin/participants/{id}/status or /api/admin/registrations/{id}/status
        if (pathInfo.contains("/participants/") || pathInfo.contains("/registrations/")) {
            String[] pParts = pathInfo.split("/");
            if (pParts.length >= 4 && "status".equalsIgnoreCase(pParts[3])) {
                try {
                    int regId = Integer.parseInt(pParts[2]);
                    JsonObject json = readJsonBody(req, JsonObject.class);
                    if (json == null || !json.has("status")) {
                        writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "status required");
                        return;
                    }
                    String newStatus = json.get("status").getAsString().trim().toUpperCase();
                    boolean ok = registrationDao.updateRegistrationStatus(regId, newStatus);
                    if (ok) {
                        writeSuccess(resp, "Participant registration status updated to " + newStatus, null);
                    } else {
                        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Registration record not found");
                    }
                    return;
                } catch (Exception e) {
                    writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                    return;
                }
            }
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not found");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        if (pathInfo.endsWith("/remove")) {
            pathInfo = pathInfo.substring(0, pathInfo.length() - "/remove".length());
        }

        // DELETE /api/admin/sessions/{id}
        if (pathInfo.startsWith("/sessions/")) {
            String targetSessionId = pathInfo.substring("/sessions/".length());
            boolean ok = SessionRegistry.terminateSession(targetSessionId);
            if (ok) {
                writeSuccess(resp, "Session terminated successfully", null);
            } else {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Session not found or already inactive");
            }
            return;
        }

        // DELETE /api/admin/participants/{id} or /api/admin/registrations/{id}
        if (pathInfo.startsWith("/participants/") || pathInfo.startsWith("/registrations/")) {
            try {
                String[] parts = pathInfo.split("/");
                int regId = Integer.parseInt(parts[2]);
                boolean ok = registrationDao.deleteRegistration(regId);
                if (ok) {
                    writeSuccess(resp, "Participant removed from event successfully", null);
                } else {
                    writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Registration record not found");
                }
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                return;
            }
        }

        // DELETE /api/admin/sessions/{sessionId}
        if (pathInfo.startsWith("/sessions/")) {
            String targetSessionId = pathInfo.substring("/sessions/".length());
            if (targetSessionId.endsWith("/terminate")) {
                targetSessionId = targetSessionId.substring(0, targetSessionId.length() - "/terminate".length());
            }
            boolean ok = SessionRegistry.terminateSession(targetSessionId);
            if (ok) {
                writeSuccess(resp, "Session terminated successfully", null);
            } else {
                writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Session not found or already inactive");
            }
            return;
        }

        // DELETE /api/admin/clubs/{clubId}/members/{userId}
        if (pathInfo.startsWith("/clubs/") && pathInfo.contains("/members/")) {
            try {
                String[] parts = pathInfo.split("/");
                int clubId = Integer.parseInt(parts[2]);
                int userId = Integer.parseInt(parts[4]);
                boolean ok = membershipDao.removeMembership(clubId, userId);
                if (ok) {
                    writeSuccess(resp, "Student removed from club successfully", null);
                } else {
                    writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Membership record not found");
                }
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                return;
            }
        }

        // DELETE /api/admin/clubs/{id}
        if (pathInfo.startsWith("/clubs/")) {
            try {
                String[] parts = pathInfo.split("/");
                int clubId = Integer.parseInt(parts[2]);
                boolean ok = clubDao.delete(clubId);
                if (ok) {
                    writeSuccess(resp, "Club deleted successfully", null);
                } else {
                    writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Club not found");
                }
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                return;
            }
        }

        // DELETE /api/admin/events/{id}
        if (pathInfo.startsWith("/events/")) {
            try {
                String[] parts = pathInfo.split("/");
                int eventId = Integer.parseInt(parts[2]);
                boolean ok = eventDao.delete(eventId);
                if (ok) {
                    writeSuccess(resp, "Event deleted successfully", null);
                } else {
                    writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Event not found");
                }
                return;
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR", e.getMessage());
                return;
            }
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not found");
    }
}
