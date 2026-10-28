package com.collegeclub.listener;

import com.collegeclub.util.SessionRegistry;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Application Lifecycle Listener for HTTP Sessions.
 * Demonstrates standard Jakarta Servlet 6.0 HttpSessionListener API.
 * Automatically catches container session creation and expiration events.
 */
@WebListener
public class UserSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Container created an unauthenticated session; registration happens on login
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if (se != null && se.getSession() != null) {
            String sessionId = se.getSession().getId();
            SessionRegistry.removeSession(sessionId);
        }
    }
}
