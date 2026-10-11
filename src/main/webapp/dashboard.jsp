<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // Ensure student details are present in request scope for rendering
    com.collegeclub.model.User currentUser = (com.collegeclub.model.User) session.getAttribute("user");
    if (currentUser != null) {
        com.collegeclub.service.impl.ClubService cs = new com.collegeclub.service.impl.ClubService();
        com.collegeclub.service.impl.EventService es = new com.collegeclub.service.impl.EventService();
        request.setAttribute("myClubs", cs.getStudentClubs(currentUser.getUserId()));
        request.setAttribute("allClubs", cs.getAllClubs());
        request.setAttribute("upcomingEvents", es.getUpcomingEvents());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - College Club Management System (JSP)</title>
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
                    <div class="brand-sub">JSP Dashboard</div>
                </div>
            </div>

            <div class="user-profile-preview">
                <div class="avatar-circle">ST</div>
                <div class="user-meta">
                    <div class="name"><c:out value="${sessionScope.user.fullName != null ? sessionScope.user.fullName : sessionScope.user.username}" /></div>
                    <span class="role-tag"><c:out value="${sessionScope.user.role}" /></span>
                </div>
            </div>

            <nav class="sidebar-nav">
                <a href="dashboard.jsp" class="nav-link active" style="text-decoration: none;">
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
                <a href="profile.jsp" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">👤</span>
                    <span>My Profile</span>
                </a>
                <a href="demo.html" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">⚡</span>
                    <span>XML &amp; JSON Lab</span>
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
                <h1>Welcome, <c:out value="${sessionScope.user.fullName != null ? sessionScope.user.fullName : sessionScope.user.username}" />!</h1>
                <p style="color: var(--text-muted); margin-bottom: 0;">
                    Server-side authenticated session via <code>HttpSession</code> on Apache Tomcat.
                    Logged in as <strong><c:out value="${sessionScope.user.username}" /></strong>.
                </p>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon primary">🎯</div>
                    <div class="stat-info">
                        <span class="stat-label">Enrolled Clubs</span>
                        <span class="stat-value"><c:out value="${myClubs != null ? myClubs.size() : 0}" /></span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon accent">🎟️</div>
                    <div class="stat-info">
                        <span class="stat-label">Active Campus Clubs</span>
                        <span class="stat-value"><c:out value="${allClubs != null ? allClubs.size() : 0}" /></span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon success">🚀</div>
                    <div class="stat-info">
                        <span class="stat-label">Upcoming Events</span>
                        <span class="stat-value"><c:out value="${upcomingEvents != null ? upcomingEvents.size() : 0}" /></span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon warning">🔒</div>
                    <div class="stat-info">
                        <span class="stat-label">Session ID</span>
                        <span class="stat-value" style="font-size: 0.95rem; font-family: monospace;">
                            <c:out value="${pageContext.session.id.substring(0, Math.min(10, pageContext.session.id.length()))}..." />
                        </span>
                    </div>
                </div>
            </div>

            <!-- Enrolled Clubs Section -->
            <section style="margin-top: 2.5rem;">
                <div class="section-header">
                    <div>
                        <h2>My Clubs</h2>
                        <p class="section-subtitle">Clubs you are actively enrolled in</p>
                    </div>
                    <a href="clubs.html" class="btn btn-secondary btn-sm">Explore More</a>
                </div>

                <div class="cards-grid">
                    <c:choose>
                        <c:when test="${not empty myClubs}">
                            <c:forEach var="c" items="${myClubs}">
                                <div class="card">
                                    <span class="badge badge-accent" style="margin-bottom: 0.5rem; display: inline-block;">
                                        <c:out value="${c.category}" />
                                    </span>
                                    <h3 style="font-size: 1.25rem; font-weight: 700; color: #fff; margin-bottom: 0.5rem;">
                                        <c:out value="${c.clubName}" />
                                    </h3>
                                    <p style="color: var(--text-muted); font-size: 0.88rem; margin-bottom: 1.25rem;">
                                        <c:out value="${c.description}" />
                                    </p>
                                    <div style="font-size: 0.8rem; color: var(--text-dim); border-top: 1px solid rgba(255, 255, 255, 0.08); padding-top: 0.75rem;">
                                        Coordinator: <span style="color: #38bdf8;"><c:out value="${c.coordinatorName}" /></span>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div style="grid-column: 1 / -1; padding: 2.5rem; text-align: center; color: var(--text-muted); background: var(--bg-card); border-radius: var(--radius-md); border: var(--border-glass);">
                                <p style="margin-bottom: 1rem;">You have not joined any campus clubs yet.</p>
                                <a href="clubs.html" class="btn btn-primary btn-sm">Browse Club Directory</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>

            <!-- Upcoming Events Section -->
            <section style="margin-top: 2.5rem;">
                <div class="section-header">
                    <div>
                        <h2>Upcoming Events</h2>
                        <p class="section-subtitle">Conferences, workshops, and inter-collegiate events</p>
                    </div>
                    <a href="events.html" class="btn btn-secondary btn-sm">All Events</a>
                </div>

                <div class="cards-grid">
                    <c:choose>
                        <c:when test="${not empty upcomingEvents}">
                            <c:forEach var="ev" items="${upcomingEvents}">
                                <div class="card">
                                    <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                                        <span class="badge badge-primary"><c:out value="${ev.clubName}" /></span>
                                        <span class="badge badge-success"><c:out value="${ev.status}" /></span>
                                    </div>
                                    <h3 style="font-size: 1.2rem; color: #fff; margin-bottom: 0.4rem;">
                                        <c:out value="${ev.title}" />
                                    </h3>
                                    <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 1rem;">
                                        <c:out value="${ev.description}" />
                                    </p>
                                    <div style="font-size: 0.8rem; color: var(--text-dim); margin-bottom: 1rem;">
                                        📍 Venue: <c:out value="${ev.venue}" /> &nbsp;|&nbsp; 👥 Capacity: <c:out value="${ev.capacity}" />
                                    </div>
                                    <a href="event.html?id=${ev.eventId}" class="btn btn-secondary btn-sm btn-block">View Details</a>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div style="grid-column: 1 / -1; padding: 2rem; text-align: center; color: var(--text-muted);">
                                No upcoming events scheduled at this moment.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
