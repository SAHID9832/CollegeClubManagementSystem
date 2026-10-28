package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.service.impl.UserService;
import com.collegeclub.util.SessionRegistry;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

/**
 * Authentication Controller (Servlets + REST API)
 * Mapped to /login and /api/auth/login
 * Validates credentials via PBKDF2 hashing, establishes secure HttpSession,
 * calls request.changeSessionId() for session fixation defense, and stores CSRF token.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/api/auth/login", "/api/auth/forgot-password"})
public class LoginServlet extends BaseApiServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        if (uri.contains("forgot-password")) {
            handleForgotPassword(req, resp);
            return;
        }

        String contentType = req.getContentType();
        String username;
        String password;

        if (contentType != null && contentType.contains("application/json")) {
            JsonObject json = readJsonBody(req, JsonObject.class);
            if (json == null) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "JSON body cannot be empty");
                return;
            }
            username = json.has("username") && !json.get("username").isJsonNull() ? json.get("username").getAsString() : "";
            password = json.has("password") && !json.get("password").isJsonNull() ? json.get("password").getAsString() : "";
        } else {
            username = req.getParameter("username");
            password = req.getParameter("password");
        }

        try {
            User user = userService.login(username, password);

            // Session Management: Invalidate old session and create fresh one
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = req.getSession(true);
            // Defend against session fixation
            try {
                req.changeSessionId();
            } catch (IllegalStateException ignored) {}

            String csrfToken = UUID.randomUUID().toString();
            session.setAttribute("user", user);
            session.setAttribute("csrfToken", csrfToken);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout

            // Register active session for administrative monitoring
            SessionRegistry.registerSession(session, user, req.getRemoteAddr());

            if (contentType != null && contentType.contains("application/json")) {
                JsonObject resData = new JsonObject();
                resData.addProperty("token", csrfToken);
                resData.addProperty("sessionId", session.getId());
                resData.addProperty("userId", user.getUserId());
                resData.addProperty("username", user.getUsername());
                resData.addProperty("fullName", user.getFullName());
                resData.addProperty("role", user.getRole().name());
                resData.addProperty("csrfToken", csrfToken);

                writeSuccess(resp, "Login successful", resData);
            } else {
                if (user.getRole() == Role.ADMIN) {
                    resp.sendRedirect(req.getContextPath() + "/admin.jsp");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/dashboard.jsp");
                }
            }
        } catch (ClubSystemException e) {
            if (contentType != null && contentType.contains("application/json")) {
                writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getClass().getSimpleName(), e.getMessage());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.html?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            if (contentType != null && contentType.contains("application/json")) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Login error: " + e.getMessage());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.html?error=" + java.net.URLEncoder.encode("Internal error", java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = "STUDENT";
        String username = "";
        String verification = "";
        String newPassword = "";

        String contentType = req.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            JsonObject json = readJsonBody(req, JsonObject.class);
            if (json == null) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "JSON body cannot be empty");
                return;
            }
            if (json.has("role") && !json.get("role").isJsonNull()) role = json.get("role").getAsString();
            if (json.has("username") && !json.get("username").isJsonNull()) username = json.get("username").getAsString();
            if (json.has("verification") && !json.get("verification").isJsonNull()) verification = json.get("verification").getAsString();
            if (json.has("newPassword") && !json.get("newPassword").isJsonNull()) newPassword = json.get("newPassword").getAsString();
        } else {
            role = req.getParameter("role") != null ? req.getParameter("role") : "STUDENT";
            username = req.getParameter("username") != null ? req.getParameter("username") : "";
            verification = req.getParameter("verification") != null ? req.getParameter("verification") : "";
            newPassword = req.getParameter("newPassword") != null ? req.getParameter("newPassword") : "";
        }

        try {
            boolean success = userService.resetPassword(role, username, verification, newPassword);
            if (success) {
                JsonObject data = new JsonObject();
                data.addProperty("username", username);
                data.addProperty("role", role);
                writeSuccess(resp, "Password reset successfully! You can now log in with your new password.", data);
            } else {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "RESET_FAILED", "Failed to update password. Please try again.");
            }
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Password reset error: " + e.getMessage());
        }
    }
}
