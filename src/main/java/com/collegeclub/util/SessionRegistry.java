package com.collegeclub.util;

import com.collegeclub.model.User;
import com.collegeclub.model.UserSessionInfo;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe In-Memory Session Registry.
 * Tracks active HTTP Sessions, maps session IDs to authenticated users,
 * and allows administrators to monitor and invalidate active sessions in real-time.
 */
public class SessionRegistry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Map of SessionId -> UserSessionInfo metadata
    private static final Map<String, UserSessionInfo> sessionData = new ConcurrentHashMap<>();

    // Map of SessionId -> active HttpSession reference for programmatic termination
    private static final Map<String, HttpSession> liveSessions = new ConcurrentHashMap<>();

    private SessionRegistry() {}

    /**
     * Registers a freshly authenticated user session.
     */
    public static void registerSession(HttpSession session, User user, String ipAddress) {
        if (session == null || user == null) return;

        String sessionId = session.getId();
        String now = LocalDateTime.now().format(FORMATTER);
        String clientIp = (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : "127.0.0.1";

        UserSessionInfo info = new UserSessionInfo(
                sessionId,
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole() != null ? user.getRole().name() : "STUDENT",
                now,
                now,
                clientIp,
                true
        );

        sessionData.put(sessionId, info);
        liveSessions.put(sessionId, session);
    }

    /**
     * Updates the last access timestamp of an active session.
     */
    public static void updateAccess(String sessionId) {
        if (sessionId == null) return;
        UserSessionInfo info = sessionData.get(sessionId);
        if (info != null && info.isActive()) {
            info.setLastAccessTime(LocalDateTime.now().format(FORMATTER));
        }
    }

    /**
     * Called when a session is invalidated or user logs out.
     */
    public static void removeSession(String sessionId) {
        if (sessionId == null) return;
        UserSessionInfo info = sessionData.get(sessionId);
        if (info != null) {
            info.setActive(false);
            info.setLastAccessTime(LocalDateTime.now().format(FORMATTER));
        }
        liveSessions.remove(sessionId);
    }

    /**
     * Programmatically terminates an active session by the Administrator.
     * Calls session.invalidate() and marks the session inactive.
     */
    public static boolean terminateSession(String sessionId) {
        if (sessionId == null) return false;
        HttpSession session = liveSessions.remove(sessionId);
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
                // Already invalidated
            }
        }
        UserSessionInfo info = sessionData.get(sessionId);
        if (info != null) {
            info.setActive(false);
            info.setLastAccessTime(LocalDateTime.now().format(FORMATTER));
            return true;
        }
        return false;
    }

    /**
     * Returns all registered sessions (newest first).
     */
    public static List<UserSessionInfo> getAllSessions() {
        List<UserSessionInfo> list = new ArrayList<>(sessionData.values());
        list.sort(Comparator.comparing(UserSessionInfo::getLoginTime, Comparator.reverseOrder()));
        return list;
    }

    /**
     * Returns only currently active sessions.
     */
    public static List<UserSessionInfo> getActiveSessions() {
        List<UserSessionInfo> list = new ArrayList<>();
        for (UserSessionInfo info : sessionData.values()) {
            if (info.isActive()) {
                list.add(info);
            }
        }
        list.sort(Comparator.comparing(UserSessionInfo::getLoginTime, Comparator.reverseOrder()));
        return list;
    }

    /**
     * Count of active sessions.
     */
    public static int getActiveCount() {
        int count = 0;
        for (UserSessionInfo info : sessionData.values()) {
            if (info.isActive()) count++;
        }
        return count;
    }
}
