package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Event;
import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import com.collegeclub.service.impl.EventService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Event Operations Controller (REST API)
 * Mapped to /api/events/*
 * Handles event catalog, scheduling, event registration, and cancellation.
 */
@WebServlet(name = "EventServlet", urlPatterns = {"/api/events", "/api/events/*"})
public class EventServlet extends BaseApiServlet {

    private final EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            // GET /api/events
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                String clubIdParam = req.getParameter("clubId");
                String status = req.getParameter("status");

                List<Event> events;
                if (clubIdParam != null && !clubIdParam.trim().isEmpty()) {
                    events = eventService.getEventsByClub(Integer.parseInt(clubIdParam.trim()));
                } else if ("OPEN".equalsIgnoreCase(status) || "UPCOMING".equalsIgnoreCase(status)) {
                    events = eventService.getUpcomingEvents();
                } else {
                    events = eventService.getAllEvents();
                }

                writeSuccess(resp, "Events retrieved", events);
                return;
            }

            // GET /api/events/{id}
            String[] parts = pathInfo.split("/");
            if (parts.length >= 2) {
                int eventId = Integer.parseInt(parts[1]);
                Event event = eventService.getEventById(eventId);
                writeSuccess(resp, "Event details", event);
                return;
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Endpoint not found");
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Event ID must be numeric");
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
            // POST /api/events - Schedule new event
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.COORDINATOR)) {
                    writeError(resp, HttpServletResponse.SC_FORBIDDEN, "FORBIDDEN", "Only Admin or Coordinator can schedule events");
                    return;
                }

                JsonObject json = readJsonBody(req, JsonObject.class);
                if (json == null) {
                    writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_REQUEST", "Payload required");
                    return;
                }

                int clubId = json.has("clubId") ? json.get("clubId").getAsInt() : 1;
                String title = json.has("title") ? json.get("title").getAsString() : "";
                String description = json.has("description") ? json.get("description").getAsString() : "";
                String dateStr = json.has("eventDate") ? json.get("eventDate").getAsString() : LocalDateTime.now().plusDays(7).toString();
                String venue = json.has("venue") ? json.get("venue").getAsString() : "Main Auditorium";
                int capacity = json.has("capacity") ? json.get("capacity").getAsInt() : 100;

                LocalDateTime eventDate;
                try {
                    eventDate = LocalDateTime.parse(dateStr.replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e) {
                    eventDate = LocalDateTime.now().plusDays(7);
                }

                Event created = eventService.scheduleEvent(clubId, title, description, eventDate, venue, capacity);
                writeSuccess(resp, "Event scheduled successfully", created);
                return;
            }

            // Path pattern: /{id}/register
            String[] parts = pathInfo.split("/");
            if (parts.length >= 3) {
                int eventId = Integer.parseInt(parts[1]);
                String action = parts[2];

                if (currentUser == null) {
                    writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in to register for events");
                    return;
                }

                if ("register".equalsIgnoreCase(action)) {
                    eventService.registerForEvent(eventId, currentUser.getUserId());
                    writeSuccess(resp, "Successfully registered for event!", null);
                    return;
                } else if ("cancel".equalsIgnoreCase(action)) {
                    eventService.cancelEventRegistration(eventId, currentUser.getUserId());
                    writeSuccess(resp, "Event registration cancelled.", null);
                    return;
                }
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not recognized");
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Event ID must be numeric");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        User currentUser = getAuthenticatedUser(req);

        if (currentUser == null) {
            writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "Please log in first");
            return;
        }

        try {
            // DELETE /api/events/{id}/register
            String[] parts = pathInfo != null ? pathInfo.split("/") : new String[0];
            if (parts.length >= 3 && "register".equalsIgnoreCase(parts[2])) {
                int eventId = Integer.parseInt(parts[1]);
                eventService.cancelEventRegistration(eventId, currentUser.getUserId());
                writeSuccess(resp, "Event registration cancelled successfully.", null);
                return;
            }

            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Resource not found");
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ID", "Event ID must be numeric");
        } catch (ClubSystemException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
        } catch (Exception e) {
            writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
        }
    }
}
