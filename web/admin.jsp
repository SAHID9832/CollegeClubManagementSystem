<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    com.collegeclub.dao.impl.UserDaoImpl userDao = new com.collegeclub.dao.impl.UserDaoImpl();
    com.collegeclub.dao.impl.ClubDaoImpl clubDao = new com.collegeclub.dao.impl.ClubDaoImpl();
    com.collegeclub.dao.impl.EventDaoImpl eventDao = new com.collegeclub.dao.impl.EventDaoImpl();

    request.setAttribute("userList", userDao.getAllUsers());
    request.setAttribute("clubList", clubDao.getAllClubs());
    request.setAttribute("eventList", eventDao.getAllEvents());
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Portal - College Club Management System (JSP)</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <img src="images/campussphere-logo.png" alt="CampusSphere" class="brand-logo-img">
                <div>
                    <div class="brand-text">CampusSphere</div>
                    <div class="brand-sub">Administration</div>
                </div>
            </div>

            <div class="user-profile-preview">
                <div class="avatar-circle" style="background: linear-gradient(135deg, #f43f5e, #e11d48);">AD</div>
                <div class="user-meta">
                    <div class="name"><c:out value="${sessionScope.user.fullName != null ? sessionScope.user.fullName : sessionScope.user.username}" /></div>
                    <span class="role-tag" style="background: rgba(244, 63, 94, 0.2); color: #f43f5e;">ADMINISTRATOR</span>
                </div>
            </div>

            <nav class="sidebar-nav">
                <a href="admin.jsp" class="nav-link active" style="text-decoration: none;">
                    <span class="nav-icon">📊</span>
                    <span>Admin Overview</span>
                </a>
                <a href="dashboard.jsp" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">🎓</span>
                    <span>Student View</span>
                </a>
                <a href="api/admin/reports/csv" class="nav-link" style="text-decoration: none;">
                    <span class="nav-icon">📥</span>
                    <span>Export CSV</span>
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
                <h1>System Administration Dashboard</h1>
                <p style="color: var(--text-muted); margin-bottom: 0;">
                    Manage registered students, faculty coordinators, club accreditations, and events.
                </p>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon primary">👥</div>
                    <div class="stat-info">
                        <span class="stat-label">Total Users</span>
                        <span class="stat-value"><c:out value="${userList.size()}" /></span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon accent">🎯</div>
                    <div class="stat-info">
                        <span class="stat-label">Campus Clubs</span>
                        <span class="stat-value"><c:out value="${clubList.size()}" /></span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon success">📅</div>
                    <div class="stat-info">
                        <span class="stat-label">Active Events</span>
                        <span class="stat-value"><c:out value="${eventList.size()}" /></span>
                    </div>
                </div>
            </div>

            <!-- User Management Table -->
            <section style="margin-top: 2.5rem;">
                <div class="section-header">
                    <div>
                        <h2>Registered Users</h2>
                        <p class="section-subtitle">Database records retrieved with PreparedStatement and rendered using JSTL</p>
                    </div>
                    <a href="api/admin/reports/csv" class="btn btn-secondary btn-sm">Download CSV Report</a>
                </div>

                <div class="table-container" style="background: var(--bg-card); border-radius: var(--radius-lg); border: var(--border-glass); padding: 1rem; overflow-x: auto;">
                    <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem;">
                        <thead>
                            <tr style="border-bottom: 1px solid rgba(255, 255, 255, 0.1); text-align: left; color: #38bdf8;">
                                <th style="padding: 0.75rem 1rem;">ID</th>
                                <th style="padding: 0.75rem 1rem;">Username</th>
                                <th style="padding: 0.75rem 1rem;">Full Name</th>
                                <th style="padding: 0.75rem 1rem;">Email</th>
                                <th style="padding: 0.75rem 1rem;">Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${userList}">
                                <tr style="border-bottom: 1px solid rgba(255, 255, 255, 0.05);">
                                    <td style="padding: 0.75rem 1rem;"><c:out value="${u.userId}" /></td>
                                    <td style="padding: 0.75rem 1rem; font-weight: 600; color: #fff;"><c:out value="${u.username}" /></td>
                                    <td style="padding: 0.75rem 1rem;"><c:out value="${u.fullName != null ? u.fullName : '-'}" /></td>
                                    <td style="padding: 0.75rem 1rem; color: var(--text-muted);"><c:out value="${u.email}" /></td>
                                    <td style="padding: 0.75rem 1rem;">
                                        <span class="badge ${u.role == 'ADMIN' ? 'badge-danger' : (u.role == 'COORDINATOR' ? 'badge-accent' : 'badge-primary')}">
                                            <c:out value="${u.role}" />
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
