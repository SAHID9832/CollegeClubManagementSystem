package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.service.impl.ClubService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Club Operations Controller (REST API)
 * Mapped to /api/clubs/*
 * Handles club discovery, details, creation, joining, leaving, and membership list.
 */
@WebServlet(name = "ClubServlet", urlPatterns = {"/api/clubs", "/api/clubs/*"})
public class ClubServlet extends BaseApiServlet {

    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            // GET /api/clubs (or /api/clubs/)
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                String category = req.getParameter("category");
                String search = req.getParameter("search");

                List<Club> clubs;
                if (search != null && !search.trim().isEmpty()) {
                    clubs = clubService.searchClubs(search.trim());
                } else if (category != null && !category.trim().isEmpty()) {
                    clubs = clubService.getClubsByCategory(category.trim());
                } else {
                    clubs = clubService.getAllClubs();
                }
                writeSuccess(resp, "Clubs retrieved", clubs);
                return;
            }

            // Path pattern: /{id} or /{id}/members
            String[] parts = pathInfo.split("/");
            if (parts.length >= 2) {
                int clubId = Integer.parseInt(parts[1]);

                if (parts.length == 3 && "members".equalsIgnoreCase(parts[2])) {
                    // GET /api/clubs/{id}/members
                    writeSuccess(resp, "Club members", clubService.getClubMembers(clubId));
                    return;
                }

                // GET /api/clubs/{id}
                Club club = clubService.getClubById(clubId);
                writeSuccess(resp, "Club details", club);
                return;
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Endpoint not found");
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Club ID must be numeric");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        User currentUser = getAuthenticatedUser(req);

        try {
            // POST /api/clubs - Create new club (Admin / Coordinator only)
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.COORDINATOR)) {
                    writeError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Only Admin or Coordinator can create clubs");
                    return;
                }

                JsonObject json = readJsonBody(req, JsonObject.class);
                if (json == null) {
                    writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "Payload required");
                    return;
                }

                String clubName = json.has("clubName") ? json.get("clubName").getAsString() : "";
                String category = json.has("category") ? json.get("category").getAsString() : "General";
                String description = json.has("description") ? json.get("description").getAsString() : "";
                int coordinatorId = json.has("coordinatorId") ? json.get("coordinatorId").getAsInt() : currentUser.getUserId();
                int capacity = json.has("capacity") ? json.get("capacity").getAsInt() : 50;

                Club created = clubService.createClub(clubName, category, description, coordinatorId, capacity);
                writeSuccess(resp, "Club created successfully", created);
                return;
            }

            // Path pattern: /{id}/join or /{id}/leave
            String[] parts = pathInfo.split("/");
            if (parts.length >= 3) {
                int clubId = Integer.parseInt(parts[1]);
                String action = parts[2];

                if (currentUser == null) {
                    writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to join or leave a club");
                    return;
                }

                if ("join".equalsIgnoreCase(action)) {
                    clubService.joinClub(clubId, currentUser.getUserId());
                    writeSuccess(resp, "Successfully joined club!", null);
                    return;
                } else if ("leave".equalsIgnoreCase(action)) {
                    clubService.leaveClub(clubId, currentUser.getUserId());
                    writeSuccess(resp, "Successfully left club.", null);
                    return;
                }
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not recognized");
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Club ID must be numeric");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }
}
