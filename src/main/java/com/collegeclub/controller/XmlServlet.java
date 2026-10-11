package com.collegeclub.controller;

import com.collegeclub.model.Club;
import com.collegeclub.service.impl.ClubService;
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
 * Dedicated XML Operations Controller
 * Mapped to /api/xml/*
 * Implements Form Navigation receiver and XML catalog generator
 * corresponding to faculty XML lecture slides (3_5_XML-JCM-2.pptx).
 */
@WebServlet(name = "XmlServlet", urlPatterns = {"/api/xml", "/api/xml/*"})
public class XmlServlet extends BaseApiServlet {

    private final ClubService clubService = new ClubService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        // GET /api/xml/clubs - Export live clubs catalog in XML
        if (pathInfo.equals("/clubs") || pathInfo.equals("") || pathInfo.equals("/")) {
            try {
                List<Club> clubs = clubService.getAllClubs();
                String xml = XmlUtil.clubsToXml(clubs);

                resp.setContentType("application/xml;charset=UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(xml);
                out.flush();
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "XML_ERROR", e.getMessage());
            }
            return;
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "XML resource not found");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // POST /api/xml/student - Process student XML submission from Form Navigation
        if (pathInfo != null && pathInfo.equals("/student")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            String xmlPayload = sb.toString().trim();
            if (xmlPayload.isEmpty()) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "EMPTY_PAYLOAD", "XML request body is empty");
                return;
            }

            try {
                Map<String, String> data = XmlUtil.parseFacultyStudentXml(xmlPayload);

                JsonObject studentObj = new JsonObject();
                studentObj.addProperty("name", data.get("name"));
                studentObj.addProperty("rollno", data.get("rollno"));
                studentObj.addProperty("course", data.get("course"));
                try {
                    studentObj.addProperty("age", Integer.parseInt(data.get("age")));
                } catch (Exception e) {
                    studentObj.addProperty("age", data.get("age"));
                }

                writeSuccess(resp, "XML student payload parsed successfully by Jakarta Servlet", studentObj);
            } catch (Exception e) {
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "XML_PARSE_ERROR", "Invalid XML structure: " + e.getMessage());
            }
            return;
        }

        writeError(resp, HttpServletResponse.SC_NOT_FOUND, "NOT_FOUND", "Endpoint not found");
    }
}
