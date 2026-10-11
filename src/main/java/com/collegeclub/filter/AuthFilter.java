package com.collegeclub.filter;

import com.collegeclub.model.Role;
import com.collegeclub.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication and Access Control Filter
 * Restricts access to JSP dashboards and administrative portals
 * strictly to authenticated users with appropriate roles.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/dashboard.jsp", "/admin.jsp", "/profile.jsp"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String requestUri = req.getRequestURI();

        if (user == null) {
            // Unauthenticated: redirect to login
            String loginUrl = req.getContextPath() + "/login.html?returnUrl=" +
                    java.net.URLEncoder.encode(requestUri, java.nio.charset.StandardCharsets.UTF_8);
            resp.sendRedirect(loginUrl);
            return;
        }

        // Admin dashboard requires ADMIN role
        if (requestUri.endsWith("admin.jsp") && user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Administrator privileges required");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
