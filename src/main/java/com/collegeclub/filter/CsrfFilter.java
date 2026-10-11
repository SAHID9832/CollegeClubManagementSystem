package com.collegeclub.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Cross-Site Request Forgery (CSRF) Protection Filter
 * Verifies X-CSRF-Token header or parameter for authenticated state-changing requests.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/api/*"})
public class CsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String method = req.getMethod();
        // Safe read-only HTTP methods are exempt
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        String path = req.getRequestURI();
        // Public unauthenticated endpoints exempt
        if (path.contains("/api/auth/login") ||
            path.contains("/api/auth/register") ||
            path.contains("/api/xml/student")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            String sessionCsrf = (String) session.getAttribute("csrfToken");
            String reqCsrf = req.getHeader("X-CSRF-Token");
            if (reqCsrf == null || reqCsrf.trim().isEmpty()) {
                reqCsrf = req.getHeader("Authorization");
                if (reqCsrf != null && reqCsrf.startsWith("Bearer ")) {
                    reqCsrf = reqCsrf.substring(7).trim();
                }
            }
            if (reqCsrf == null || reqCsrf.trim().isEmpty()) {
                reqCsrf = req.getParameter("csrfToken");
            }

            // If session has CSRF token and client provided one, ensure they match
            if (sessionCsrf != null && reqCsrf != null && !sessionCsrf.equals(reqCsrf)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().print("{\"success\":false,\"errorCode\":\"CSRF_MISMATCH\",\"message\":\"Invalid CSRF token\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
