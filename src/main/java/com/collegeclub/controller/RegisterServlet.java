package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Student;
import com.collegeclub.service.impl.UserService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Registration Controller (Servlets + REST API)
 * Mapped to /register and /api/auth/register
 * Enforces faculty validation rules: username >= 5, password >= 8 with digit, NO confirm password.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register", "/api/auth/register"})
public class RegisterServlet extends BaseApiServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentType = req.getContentType();
        String username;
        String password;
        String email;
        String fullName;
        String rollNumber;
        String department;
        String course;
        int year = 1;

        if (contentType != null && contentType.contains("application/json")) {
            JsonObject json = readJsonBody(req, JsonObject.class);
            if (json == null) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "JSON body cannot be empty");
                return;
            }
            username = json.has("username") && !json.get("username").isJsonNull() ? json.get("username").getAsString() : "";
            password = json.has("password") && !json.get("password").isJsonNull() ? json.get("password").getAsString() : "";
            email = json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : "";
            fullName = json.has("fullName") && !json.get("fullName").isJsonNull() ? json.get("fullName").getAsString() : "";
            rollNumber = json.has("rollNumber") && !json.get("rollNumber").isJsonNull() ? json.get("rollNumber").getAsString() : "";
            department = json.has("department") && !json.get("department").isJsonNull() ? json.get("department").getAsString() : "";
            course = json.has("course") && !json.get("course").isJsonNull() ? json.get("course").getAsString() : "";
            if (json.has("year") && !json.get("year").isJsonNull()) {
                try {
                    year = json.get("year").getAsInt();
                } catch (Exception ignored) {}
            }
        } else {
            username = req.getParameter("username");
            password = req.getParameter("password");
            email = req.getParameter("email");
            fullName = req.getParameter("fullName");
            rollNumber = req.getParameter("rollNumber");
            department = req.getParameter("department");
            course = req.getParameter("course");
            try {
                String yrStr = req.getParameter("year");
                if (yrStr != null && !yrStr.trim().isEmpty()) {
                    year = Integer.parseInt(yrStr.trim());
                }
            } catch (Exception ignored) {}
        }

        try {
            Student student = userService.registerStudent(
                    username,
                    password,
                    email != null ? email.trim() : "",
                    fullName != null ? fullName.trim() : "",
                    rollNumber != null ? rollNumber.trim() : "",
                    department != null ? department.trim() : "",
                    course != null ? course.trim() : "",
                    year
            );

            if (contentType != null && contentType.contains("application/json")) {
                JsonObject resData = new JsonObject();
                resData.addProperty("userId", student.getUserId());
                resData.addProperty("username", student.getUsername());
                resData.addProperty("email", student.getEmail());
                resData.addProperty("fullName", student.getFullName());
                resData.addProperty("role", student.getRole().name());
                writeSuccess(resp, "Registration successful!", resData);
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.html?registered=true");
            }
        } catch (ClubSystemException e) {
            if (contentType != null && contentType.contains("application/json")) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
            } else {
                resp.sendRedirect(req.getContextPath() + "/register.html?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            if (contentType != null && contentType.contains("application/json")) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Registration error: " + e.getMessage());
            } else {
                resp.sendRedirect(req.getContextPath() + "/register.html?error=" + java.net.URLEncoder.encode("Internal error", java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }
}
