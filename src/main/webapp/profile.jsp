<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    com.collegeclub.model.User currentUser = (com.collegeclub.model.User) session.getAttribute("user");
    if (currentUser != null) {
        com.collegeclub.service.impl.UserService us = new com.collegeclub.service.impl.UserService();
        try {
            request.setAttribute("studentProfile", us.getStudentByUserId(currentUser.getUserId()));
        } catch (Exception ignored) {}
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - College Club Management System (JSP)</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <div class="logo-badge">🏛️</div>
                <div>
                    <div class="brand-text">CampusHub</div>
                    <div class="brand-sub">Student Profile</div>
                </div>
            </div>

            <nav class="sidebar-nav">
                <a href="dashboard.jsp" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">📊</span>
                    <span>Dashboard</span>
                </a>
                <a href="clubs.html" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">🎯</span>
                    <span>Explore Clubs</span>
                </a>
                <a href="events.html" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">📅</span>
                    <span>Events</span>
                </a>
                <a href="profile.jsp" class="nav-link active" style="text-decoration: none;">
                    <span class="nav-icon">👤</span>
                    <span>My Profile</span>
                </a>
                <a href="demo.html" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">⚡</span>
                    <span>XML Lab</span>
                </a>
            </nav>

            <div class="sidebar-footer">
                <a href="logout" class="nav-link btn-ghost" style="color: var(--danger); text-decoration: none;">
                    <span class="nav-icon">🚪</span>
                    <span>Log Out</span>
                </a>
            </div>
        </aside>

        <!-- Main Content Area -->
        <main class="main-content">
            <div class="welcome-banner">
                <h1>Student Profile</h1>
                <p style="color: var(--text-muted); margin-bottom: 0;">
                    Academic registration and club membership credentials
                </p>
            </div>

            <div style="background: var(--bg-card); border-radius: var(--radius-lg); border: var(--border-glass); padding: 2rem; max-width: 800px; margin-top: 1.5rem;">
                <div style="display: flex; align-items: center; gap: 1.5rem; margin-bottom: 2rem; padding-bottom: 1.5rem; border-bottom: 1px solid rgba(255, 255, 255, 0.08);">
                    <div class="avatar-circle" style="width: 64px; height: 64px; font-size: 1.5rem;">
                        <c:out value="${sessionScope.user.username.substring(0, Math.min(2, sessionScope.user.username.length())).toUpperCase()}" />
                    </div>
                    <div>
                        <h2 style="font-size: 1.5rem; color: #fff; margin-bottom: 0.25rem;">
                            <c:out value="${sessionScope.user.fullName != null ? sessionScope.user.fullName : sessionScope.user.username}" />
                        </h2>
                        <span class="badge badge-primary"><c:out value="${sessionScope.user.role}" /></span>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">Username</div>
                        <div style="font-size: 1.05rem; font-weight: 600; color: #fff; margin-top: 0.25rem;">
                            <c:out value="${sessionScope.user.username}" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">Email Address</div>
                        <div style="font-size: 1.05rem; font-weight: 600; color: #38bdf8; margin-top: 0.25rem;">
                            <c:out value="${sessionScope.user.email}" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">Roll Number</div>
                        <div style="font-size: 1.05rem; font-weight: 600; color: #fff; margin-top: 0.25rem;">
                            <c:out value="${studentProfile != null ? studentProfile.rollNumber : 'N/A'}" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">Course &amp; Department</div>
                        <div style="font-size: 1.05rem; font-weight: 600; color: #fff; margin-top: 0.25rem;">
                            <c:out value="${studentProfile != null ? studentProfile.course : 'MCA'}" /> - <c:out value="${studentProfile != null ? studentProfile.department : 'General'}" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">Year of Study</div>
                        <div style="font-size: 1.05rem; font-weight: 600; color: #fff; margin-top: 0.25rem;">
                            Year <c:out value="${studentProfile != null ? studentProfile.year : 1}" />
                        </div>
                    </div>
                    <div>
                        <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase;">XML Export</div>
                        <div style="margin-top: 0.25rem;">
                            <a href="api/students/xml" target="_blank" class="btn btn-secondary btn-sm">Export Profile XML ↗</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>
