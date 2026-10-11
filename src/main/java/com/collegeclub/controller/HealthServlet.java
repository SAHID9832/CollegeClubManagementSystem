package com.collegeclub.controller;

import com.collegeclub.util.DBConnection;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;

/**
 * Health Check Controller
 * Mapped to /api/health
 * Verifies system availability and database connectivity.
 */
@WebServlet(name = "HealthServlet", urlPatterns = {"/api/health"})
public class HealthServlet extends BaseApiServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject health = new JsonObject();
        health.addProperty("status", "UP");
        health.addProperty("service", "College Club Management System");
        health.addProperty("version", "1.0.0");
        health.addProperty("timestamp", Instant.now().toString());

        boolean dbConnected = false;
        try (Connection conn = DBConnection.getConnection()) {
            dbConnected = conn != null && !conn.isClosed();
        } catch (Exception ignored) {}

        health.addProperty("database", dbConnected ? "CONNECTED" : "DISCONNECTED");

        if (dbConnected) {
            writeJson(resp, HttpServletResponse.SC_OK, health);
        } else {
            writeJson(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, health);
        }
    }
}
