package com.collegeclub.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Enterprise Security Headers Filter
 * Applies OWASP Recommended security headers across all responses.
 */
@WebFilter(filterName = "SecurityHeadersFilter", urlPatterns = {"/*"})
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) response;
            HttpServletRequest httpReq = (HttpServletRequest) request;

            // Prevent MIME-sniffing
            httpResp.setHeader("X-Content-Type-Options", "nosniff");
            // Clickjacking defense
            httpResp.setHeader("X-Frame-Options", "SAMEORIGIN");
            // Reflected XSS filter
            httpResp.setHeader("X-XSS-Protection", "1; mode=block");
            // Strict referrer
            httpResp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Disable caching for dynamic API responses & JSP pages
            String uri = httpReq.getRequestURI();
            if (uri.contains("/api/") || uri.endsWith(".jsp")) {
                httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                httpResp.setHeader("Pragma", "no-cache");
                httpResp.setDateHeader("Expires", 0);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
