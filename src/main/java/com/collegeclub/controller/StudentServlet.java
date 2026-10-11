package com.collegeclub.controller;

import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Student;
import com.collegeclub.model.User;
import com.collegeclub.service.impl.ClubService;
import com.collegeclub.service.impl.EventService;
import com.collegeclub.service.impl.UserService;
import com.collegeclub.util.XmlUtil;
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
 * and XML student profile export/import.
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
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/xml")) {
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

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Action not supported");
    }
}
