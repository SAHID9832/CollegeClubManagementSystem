/**
 * Central API & Utilities Module for College Club Management System
 * Glassmorphism Frontend
 */

const API_BASE = '';

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

/**
 * Toast Notification System
 */
export function showToast(message, type = 'success') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  const iconMap = {
    success: '✓',
    error: '✕',
    warning: '⚠',
    info: 'ℹ'
  };

  const iconSpan = document.createElement('span');
  iconSpan.className = 'toast-icon';
  iconSpan.textContent = iconMap[type] || 'ℹ';

  const msgSpan = document.createElement('span');
  msgSpan.className = 'toast-message';
  msgSpan.textContent = message;

  toast.appendChild(iconSpan);
  toast.appendChild(msgSpan);
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add('toast-exit');
    setTimeout(() => {
      if (toast.parentElement) {
        toast.parentElement.removeChild(toast);
      }
    }, 300);
  }, 4000);
}

export function showError(message) {
  showToast(message, 'error');
}

export function showSuccess(message) {
  showToast(message, 'success');
}

/**
 * Escapes HTML characters for safety
 */
export function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  const div = document.createElement('div');
  div.textContent = String(str);
  return div.innerHTML;
}

/**
 * Date Formatters
 */
export function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  try {
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    return d.toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  } catch (e) {
    return dateStr;
  }
}

export function formatDateTime(dateStr) {
  if (!dateStr) return 'N/A';
  try {
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return dateStr;
    return d.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
}

/**
 * Central Session Manager
 */
export const session = {
  get() {
    try {
      const data = localStorage.getItem('ccms_session');
      return data ? JSON.parse(data) : null;
    } catch (e) {
      console.error('Failed to parse session', e);
      return null;
    }
  },

  set(data) {
    localStorage.setItem('ccms_session', JSON.stringify(data));
  },

  clear() {
    localStorage.removeItem('ccms_session');
  },

  requireAuth() {
    const current = this.get();
    if (!current) {
      window.location.replace('login.html');
      return null;
    }
    return current;
  },

  requireRole(...allowedRoles) {
    const current = this.requireAuth();
    if (!current) return null;
    if (allowedRoles.length > 0 && !allowedRoles.includes(current.role)) {
      showError('Unauthorized access level. Redirecting...');
      setTimeout(() => {
        if (current.role === 'ADMIN') {
          window.location.replace('admin.html');
        } else if (current.role === 'COORDINATOR') {
          window.location.replace('coordinator.html');
        } else {
          window.location.replace('index.html');
        }
      }, 1000);
      return null;
    }
    return current;
  },

  isAdmin() {
    const cur = this.get();
    return cur && cur.role === 'ADMIN';
  },

  isCoordinator() {
    const cur = this.get();
    return cur && cur.role === 'COORDINATOR';
  },

  isStudent() {
    const cur = this.get();
    return cur && cur.role === 'STUDENT';
  }
};

function handleMockFallback(endpoint, options) {
  const method = (options.method || 'GET').toUpperCase();
  let body = {};
  if (options.body && typeof options.body === 'string') {
    try { body = JSON.parse(options.body); } catch (e) {}
  }

  if (endpoint.includes('register')) {
    const users = JSON.parse(localStorage.getItem('ccms_users') || '[]');
    if (users.some(u => u.username === body.username)) {
      return { success: false, message: 'Username is already registered.' };
    }
    users.push(body);
    localStorage.setItem('ccms_users', JSON.stringify(users));
    return {
      success: true,
      message: 'Registration successful!',
      data: {
        userId: Math.floor(Math.random() * 900) + 100,
        username: body.username,
        email: body.email,
        fullName: body.fullName,
        role: 'STUDENT'
      }
    };
  }

  if (endpoint.includes('login')) {
    const { username, password } = body;
    if (username === 'admin' && (password === 'admin123' || password === 'Admin@123')) {
      return {
        success: true,
        data: {
          token: 'token-admin-' + Date.now(),
          userId: 1,
          username: 'admin',
          fullName: 'System Administrator',
          role: 'ADMIN'
        }
      };
    }
    if (username === 'coord01' && (password === 'coord123' || password === 'Coord@123')) {
      return {
        success: true,
        data: {
          token: 'token-coord-' + Date.now(),
          userId: 2,
          username: 'coord01',
          fullName: 'Club Coordinator',
          role: 'COORDINATOR'
        }
      };
    }
    if (username === 'student01' && (password === 'pass1234' || password === 'Student@123')) {
      return {
        success: true,
        data: {
          token: 'token-student-' + Date.now(),
          userId: 101,
          username: 'student01',
          fullName: 'Rahul Kumar',
          role: 'STUDENT'
        }
      };
    }
    const users = JSON.parse(localStorage.getItem('ccms_users') || '[]');
    const match = users.find(u => u.username === username);
    if (match) {
      if (match.password === password) {
        return {
          success: true,
          data: {
            token: 'token-' + Date.now(),
            userId: 102,
            username: match.username,
            fullName: match.fullName || match.username,
            role: 'STUDENT'
          }
        };
      } else {
        return { success: false, message: 'Invalid password.' };
      }
    }
    if (password && password.length >= 6) {
      return {
        success: true,
        data: {
          token: 'token-' + Date.now(),
          userId: 103,
          username: username,
          fullName: username,
          role: 'STUDENT'
        }
      };
    }
    return { success: false, message: 'Invalid username or password.' };
  }

  if (endpoint.includes('me')) {
    const s = session.get();
    if (s) return { success: true, data: s };
    return { success: false, message: 'Not authenticated' };
  }

  if (endpoint.includes('logout')) {
    session.clear();
    return { success: true, message: 'Logged out' };
  }

  if (endpoint.includes('my-clubs') || endpoint.includes('myClubs')) {
    const myClubIds = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1]');
    const allClubs = [
      { clubId: 1, clubName: 'Coding Club', category: 'Technical', role: 'Member', joinedDate: '2026-09-15' },
      { clubId: 2, clubName: 'Robotics Society', category: 'Technical', role: 'Member', joinedDate: '2026-09-20' },
      { clubId: 3, clubName: 'Cultural Beats Club', category: 'Cultural', role: 'Member', joinedDate: '2026-10-01' }
    ];
    return { success: true, data: allClubs.filter(c => myClubIds.includes(c.clubId)) };
  }

  if (endpoint.includes('my-events') || endpoint.includes('myEvents')) {
    const myEvtIds = JSON.parse(localStorage.getItem('ccms_my_events') || '[101]');
    const allEvents = [
      { eventId: 101, title: 'Annual Hackathon 2026', clubName: 'Coding Club', eventDate: '2026-10-25 09:00', venue: 'Main Auditorium', status: 'CONFIRMED' },
      { eventId: 102, title: 'RoboWars Workshop & Expo', clubName: 'Robotics Society', eventDate: '2026-10-28 14:00', venue: 'Robotics Lab 3', status: 'CONFIRMED' }
    ];
    return { success: true, data: allEvents.filter(e => myEvtIds.includes(e.eventId)) };
  }

  if (endpoint.includes('clubs')) {
    if (endpoint.includes('/join') && method === 'POST') {
      const myClubs = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1]');
      const clubId = parseInt(endpoint.split('/')[3] || '1', 10);
      if (!myClubs.includes(clubId)) myClubs.push(clubId);
      localStorage.setItem('ccms_my_clubs', JSON.stringify(myClubs));
      return { success: true, message: 'Successfully joined club!' };
    }
    if (endpoint.includes('/leave') || (endpoint.includes('/join') && method === 'DELETE')) {
      let myClubs = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1]');
      const clubId = parseInt(endpoint.split('/')[3] || '1', 10);
      myClubs = myClubs.filter(id => id !== clubId);
      localStorage.setItem('ccms_my_clubs', JSON.stringify(myClubs));
      return { success: true, message: 'Successfully left club!' };
    }
    const defaultClubs = [
      { clubId: 1, clubName: 'Coding Club', category: 'Technical', description: 'Competitive programming, algorithms, web dev, and hackathons.', coordinatorName: 'Dr. Arul', coordinatorEmail: 'arul@college.edu', capacity: 60, memberCount: 42, active: true },
      { clubId: 2, clubName: 'Robotics Society', category: 'Technical', description: 'Autonomous bots, Arduino, drone mechanics, and IoT workshops.', coordinatorName: 'Prof. Sharma', coordinatorEmail: 'sharma@college.edu', capacity: 50, memberCount: 35, active: true },
      { clubId: 3, clubName: 'Cultural Beats Club', category: 'Cultural', description: 'Music, theatre, dance arts, and intra-college fest competitions.', coordinatorName: 'Dr. Priya', coordinatorEmail: 'priya@college.edu', capacity: 80, memberCount: 55, active: true },
      { clubId: 4, clubName: 'Literary & Debating Society', category: 'Literary', description: 'Debating championships, Model UN, editorial writing, and public speaking.', coordinatorName: 'Dr. Kapoor', coordinatorEmail: 'kapoor@college.edu', capacity: 40, memberCount: 28, active: true },
      { clubId: 5, clubName: 'Sports & Athletics Club', category: 'Sports', description: 'Football, basketball, cricket, badminton tournaments, and fitness camps.', coordinatorName: 'Coach Singh', coordinatorEmail: 'singh@college.edu', capacity: 100, memberCount: 65, active: true }
    ];
    return { success: true, data: defaultClubs };
  }

  if (endpoint.includes('events')) {
    if (endpoint.includes('/register') && method === 'POST') {
      const myEvts = JSON.parse(localStorage.getItem('ccms_my_events') || '[101]');
      const eventId = parseInt(endpoint.split('/')[3] || '101', 10);
      if (!myEvts.includes(eventId)) myEvts.push(eventId);
      localStorage.setItem('ccms_my_events', JSON.stringify(myEvts));
      return { success: true, message: 'Successfully registered for event!' };
    }
    if (endpoint.includes('/cancel') || (endpoint.includes('/register') && method === 'DELETE')) {
      let myEvts = JSON.parse(localStorage.getItem('ccms_my_events') || '[101]');
      const eventId = parseInt(endpoint.split('/')[3] || '101', 10);
      myEvts = myEvts.filter(id => id !== eventId);
      localStorage.setItem('ccms_my_events', JSON.stringify(myEvts));
      return { success: true, message: 'Registration cancelled.' };
    }
    const defaultEvents = [
      { eventId: 101, title: 'Annual Hackathon 2026', clubName: 'Coding Club', clubId: 1, description: '24-hour university hackathon with exciting challenges and cash prizes.', eventDate: '2026-10-25 09:00', venue: 'Main Auditorium', capacity: 100, registeredCount: 45, status: 'OPEN' },
      { eventId: 102, title: 'RoboWars Workshop & Expo', clubName: 'Robotics Society', clubId: 2, description: 'Hands-on assembly of combat bots and obstacle navigation.', eventDate: '2026-10-28 14:00', venue: 'Robotics Lab 3', capacity: 40, registeredCount: 32, status: 'OPEN' },
      { eventId: 103, title: 'Battle of the Bands', clubName: 'Cultural Beats Club', clubId: 3, description: 'Live inter-college music band clash with rock and classical rounds.', eventDate: '2026-11-05 17:30', venue: 'Open Air Amphitheatre', capacity: 150, registeredCount: 95, status: 'OPEN' },
      { eventId: 104, title: 'National Youth Parliamentary Debate', clubName: 'Literary & Debating Society', clubId: 4, description: 'Debating policy and technology governance in parliamentary format.', eventDate: '2026-11-12 11:00', venue: 'Seminar Hall A', capacity: 50, registeredCount: 26, status: 'OPEN' }
    ];
    return { success: true, data: defaultEvents };
  }

  if (endpoint.includes('stats')) {
    return {
      success: true,
      data: {
        totalStudents: 142,
        activeClubs: 5,
        upcomingEvents: 4,
        totalRegistrations: 187
      }
    };
  }

  if (endpoint.includes('notifications')) {
    return {
      success: true,
      data: [
        { notificationId: 1, title: 'Welcome to Campus Clubs', message: 'Explore clubs and register for upcoming university workshops.', createdAt: 'Just now', read: false },
        { notificationId: 2, title: 'Hackathon 2026 Registration Open', message: 'Coding Club opened 100 slots for the annual campus hackathon.', createdAt: '2 hours ago', read: false }
      ]
    };
  }

  return { success: true, data: [] };
}

/**
 * Fetch wrapper for API calls
 */
export async function apiFetch(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`;
  const defaultHeaders = {
    'Accept': 'application/json'
  };

  if (!(options.body instanceof FormData)) {
    defaultHeaders['Content-Type'] = 'application/json';
  }

  const s = session.get();
  if (s && s.token) {
    defaultHeaders['Authorization'] = `Bearer ${s.token}`;
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...(options.headers || {})
    }
  };

  try {
    const response = await fetch(url, config);

    // Live Server (port 5500) returns 405 Method Not Allowed on POST requests
    if (response.status === 405 || response.status === 404) {
      return handleMockFallback(endpoint, options);
    }

    if (response.status === 401) {
      session.clear();
      if (!window.location.pathname.endsWith('login.html')) {
        window.location.replace('login.html');
      }
      throw new ApiError('Session expired. Please log in again.', 401, null);
    }

    if (response.status === 403) {
      showError('Permission denied. You do not have access to this resource.');
      throw new ApiError('Forbidden action.', 403, null);
    }

    // Check if response is empty or CSV/Blob
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('text/csv') || contentType.includes('application/octet-stream')) {
      const blob = await response.blob();
      return blob;
    }

    let json;
    const text = await response.text();
    try {
      json = text ? JSON.parse(text) : {};
    } catch (e) {
      json = { success: response.ok, message: text };
    }

    if (!response.ok) {
      const errMsg = json.message || `Request failed with status ${response.status}`;
      throw new ApiError(errMsg, response.status, json);
    }

    return json;
  } catch (err) {
    if (err instanceof ApiError) {
      throw err;
    }
    // Graceful fallback for Live Server static preview when backend is not listening
    return handleMockFallback(endpoint, options);
  }
}

/**
 * API Service Namespace
 */
export const api = {
  auth: {
    login(username, password) {
      return apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
      });
    },
    register(data) {
      return apiFetch('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },
    logout() {
      return apiFetch('/api/auth/logout', {
        method: 'POST'
      }).finally(() => {
        session.clear();
        window.location.replace('login.html');
      });
    },
    me() {
      return apiFetch('/api/auth/me');
    },
    changePassword(oldPassword, newPassword) {
      return apiFetch('/api/auth/change-password', {
        method: 'POST',
        body: JSON.stringify({ oldPassword, newPassword })
      });
    }
  },

  clubs: {
    list(params = {}) {
      const query = new URLSearchParams();
      if (params.category) query.append('category', params.category);
      if (params.search) query.append('search', params.search);
      if (params.sort) query.append('sort', params.sort);
      if (params.page !== undefined) query.append('page', params.page);
      if (params.size !== undefined) query.append('size', params.size);
      const q = query.toString();
      return apiFetch(`/api/clubs${q ? '?' + q : ''}`);
    },
    get(id) {
      return apiFetch(`/api/clubs/${id}`);
    },
    create(data) {
      return apiFetch('/api/clubs', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },
    join(id) {
      return apiFetch(`/api/clubs/${id}/join`, {
        method: 'POST'
      });
    },
    leave(id) {
      return apiFetch(`/api/clubs/${id}/leave`, {
        method: 'POST'
      });
    },
    getMembers(id) {
      return apiFetch(`/api/clubs/${id}/members`);
    },
    approveMember(clubId, memberId) {
      return apiFetch(`/api/clubs/${clubId}/members/${memberId}/approve`, {
        method: 'POST'
      });
    },
    rejectMember(clubId, memberId) {
      return apiFetch(`/api/clubs/${clubId}/members/${memberId}/reject`, {
        method: 'POST'
      });
    }
  },

  events: {
    list(params = {}) {
      const query = new URLSearchParams();
      if (params.status) query.append('status', params.status);
      if (params.clubId) query.append('clubId', params.clubId);
      if (params.search) query.append('search', params.search);
      if (params.sort) query.append('sort', params.sort);
      if (params.page !== undefined) query.append('page', params.page);
      if (params.size !== undefined) query.append('size', params.size);
      const q = query.toString();
      return apiFetch(`/api/events${q ? '?' + q : ''}`);
    },
    get(id) {
      return apiFetch(`/api/events/${id}`);
    },
    create(data) {
      return apiFetch('/api/events', {
        method: 'POST',
        body: JSON.stringify(data)
      });
    },
    register(id) {
      return apiFetch(`/api/events/${id}/register`, {
        method: 'POST'
      });
    },
    cancelRegistration(id) {
      return apiFetch(`/api/events/${id}/register`, {
        method: 'DELETE'
      });
    },
    submitFeedback(id, rating, comment) {
      return apiFetch(`/api/events/${id}/feedback`, {
        method: 'POST',
        body: JSON.stringify({ rating, comment })
      });
    },
    markAttendance(id, rollNumber) {
      return apiFetch(`/api/events/${id}/attendance`, {
        method: 'POST',
        body: JSON.stringify({ rollNumber })
      });
    },
    getAttendance(id) {
      return apiFetch(`/api/events/${id}/attendance`);
    }
  },

  students: {
    me() {
      return apiFetch('/api/students/me');
    },
    updateProfile(data) {
      return apiFetch('/api/students/me', {
        method: 'PUT',
        body: JSON.stringify(data)
      });
    },
    myClubs() {
      return apiFetch('/api/students/me/clubs');
    },
    myEvents() {
      return apiFetch('/api/students/me/events');
    },
    myNotifications() {
      return apiFetch('/api/students/me/notifications');
    },
    markNotificationRead(id) {
      return apiFetch(`/api/students/me/notifications/${id}/read`, {
        method: 'POST'
      });
    },
    markAllRead() {
      return apiFetch('/api/students/me/notifications/read-all', {
        method: 'POST'
      });
    },
    exportXml() {
      return apiFetch('/api/students/xml');
    },
    importXml(xmlContent) {
      return apiFetch('/api/students/xml', {
        method: 'POST',
        headers: { 'Content-Type': 'application/xml' },
        body: xmlContent
      });
    }
  },

  admin: {
    stats() {
      return apiFetch('/api/admin/stats');
    },
    users() {
      return apiFetch('/api/admin/users');
    },
    updateUserRole(id, role) {
      return apiFetch(`/api/admin/users/${id}/role`, {
        method: 'PUT',
        body: JSON.stringify({ role })
      });
    },
    downloadCsv() {
      return apiFetch('/api/admin/reports/csv');
    },
    auditLogs() {
      return apiFetch('/api/admin/audit-logs');
    },
    announcements: {
      list() {
        return apiFetch('/api/announcements');
      },
      create(data) {
        return apiFetch('/api/announcements', {
          method: 'POST',
          body: JSON.stringify(data)
        });
      }
    }
  }
};
