package com.collegeclub.controller;

import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

/**
 * Base Abstract Servlet for REST API endpoints.
 * Provides unified JSON serialization, error reporting, request body parsing,
 * and session security utilities.
 */
public abstract class BaseApiServlet extends HttpServlet {

    protected static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

    protected void writeJson(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(GSON.toJson(data));
        out.flush();
    }

    protected void writeSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.addProperty("message", message);
        if (data != null) {
            root.add("data", GSON.toJsonTree(data));
        }
        writeJson(resp, HttpServletResponse.SC_OK, root);
    }

    protected void writeError(HttpServletResponse resp, int statusCode, String errorCode, String message) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("errorCode", errorCode);
        root.addProperty("message", message);
        root.addProperty("timestamp", Instant.now().toString());
        writeJson(resp, statusCode, root);
    }

    protected <T> T readJsonBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) {
            return null;
        }
        return GSON.fromJson(body, clazz);
    }

    protected User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object u = session.getAttribute("user");
            if (u instanceof User) {
                return (User) u;
            }
        }

        // Header fallback for SPA / API client authentication
        String uidHeader = req.getHeader("X-User-Id");
        if (uidHeader != null && !uidHeader.trim().isEmpty()) {
            try {
                int uid = Integer.parseInt(uidHeader.trim());
                if (uid > 0) {
                    com.collegeclub.dao.UserDao userDao = new com.collegeclub.dao.impl.UserDaoImpl();
                    java.util.Optional<User> uOpt = userDao.findById(uid);
                    if (uOpt.isPresent()) {
                        return uOpt.get();
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    protected boolean isAuthenticated(HttpServletRequest req) {
        return getAuthenticatedUser(req) != null;
    }

    protected boolean hasRole(HttpServletRequest req, Role... roles) {
        User u = getAuthenticatedUser(req);
        if (u == null) return false;
        for (Role r : roles) {
            if (u.getRole() == r) return true;
        }
        return false;
    }
}
