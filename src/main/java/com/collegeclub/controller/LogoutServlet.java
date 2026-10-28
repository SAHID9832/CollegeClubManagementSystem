package com.collegeclub.controller;

import com.collegeclub.util.SessionRegistry;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Logout Controller (Servlets + REST API)
 * Mapped to /logout and /api/auth/logout
 * Invalidates session and clears credentials.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout", "/api/auth/logout"})
public class LogoutServlet extends BaseApiServlet {

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            SessionRegistry.removeSession(session.getId());
            session.invalidate();
        }

        String accept = req.getHeader("Accept");
        String contentType = req.getContentType();
        if ((accept != null && accept.contains("application/json")) ||
            (contentType != null && contentType.contains("application/json"))) {
            writeSuccess(resp, "Logged out successfully", null);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.html?loggedout=true");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleLogout(req, resp);
    }
}
