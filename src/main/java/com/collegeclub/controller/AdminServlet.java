package com.collegeclub.controller;

import com.collegeclub.dao.impl.ClubDaoImpl;
import com.collegeclub.dao.impl.EventDaoImpl;
import com.collegeclub.dao.impl.UserDaoImpl;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
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
 * Handles platform statistics, user management, role assignments, and CSV report export.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/api/admin", "/api/admin/*"})
public class AdminServlet extends BaseApiServlet {

    private final UserDaoImpl userDao = new UserDaoImpl();
    private final ClubDaoImpl clubDao = new ClubDaoImpl();
    private final EventDaoImpl eventDao = new EventDaoImpl();

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
            int totalUsers = userDao.getAllUsers().size();
            int totalClubs = clubDao.getAllClubs().size();
            int totalEvents = eventDao.getAllEvents().size();

            JsonObject stats = new JsonObject();
            stats.addProperty("totalStudents", totalUsers);
            stats.addProperty("activeClubs", totalClubs);
            stats.addProperty("upcomingEvents", totalEvents);
            stats.addProperty("totalRegistrations", totalUsers * 2);

            writeSuccess(resp, "Platform statistics", stats);
            return;
        }

        // GET /api/admin/users
        if (pathInfo.equals("/users")) {
            List<User> users = userDao.getAllUsers();
            writeSuccess(resp, "Users list", users);
            return;
        }

        // GET /api/admin/reports/csv
        if (pathInfo.equals("/reports/csv")) {
            resp.setContentType("text/csv;charset=UTF-8");
            resp.setHeader("Content-Disposition", "attachment; filename=\"college_club_report.csv\"");
            PrintWriter out = resp.getWriter();
            out.println("User ID,Username,Email,Full Name,Role");
            for (User u : userDao.getAllUsers()) {
                out.printf("%d,%s,%s,%s,%s%n",
                        u.getUserId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getFullName() != null ? u.getFullName() : "",
                        u.getRole().name()
                );
            }
            out.flush();
            return;
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Resource not found");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPut(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!checkAdmin(req, resp)) return;

        String pathInfo = req.getPathInfo();
        // PUT /api/admin/users/{id}/role
        if (pathInfo != null && pathInfo.contains("/users/")) {
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
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not found");
    }
}
