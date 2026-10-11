/**
 * Central API & Utilities Module for College Club Management System
 * Glassmorphism Frontend
 */

const API_BASE = (typeof window !== 'undefined' && window.location && window.location.pathname.startsWith('/collegeclub')) ? '/collegeclub' : '';

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
          email: 'rahul.kumar@college.edu',
          rollNumber: '26MCA001',
          department: 'Computer Applications',
          course: 'MCA',
          year: 1,
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
            userId: match.userId || 102,
            username: match.username,
            fullName: match.fullName || match.username,
            email: match.email || `${match.username}@college.edu`,
            rollNumber: match.rollNumber || '26MCA042',
            department: match.department || 'Computer Applications',
            course: match.course || 'MCA',
            year: match.year || 1,
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
          email: `${username}@college.edu`,
          rollNumber: '26MCA042',
          department: 'Computer Applications',
          course: 'MCA',
          year: 1,
          role: 'STUDENT'
        }
      };
    }
    return { success: false, message: 'Invalid username or password.' };
  }

  // Define 30 default clubs for rich presentation
  const defaultClubs = [
    { clubId: 1, id: 1, name: 'Coding & Open Source Club', clubName: 'Coding & Open Source Club', category: 'Technical', icon: '💻', categoryIcon: '💻', description: 'Competitive programming, data structures, algorithms, open source contributions, and hackathon training.', coordinatorName: 'Dr. S. Arul', coordinatorEmail: 's.arul@college.edu', capacity: 120, memberCount: 78, active: true },
    { clubId: 2, id: 2, name: 'Artificial Intelligence & Robotics Club', clubName: 'Artificial Intelligence & Robotics Club', category: 'Technical', icon: '🤖', categoryIcon: '🤖', description: 'Machine learning, neural networks, computer vision, autonomous bots, and embedded robotics systems.', coordinatorName: 'Prof. K. Ramesh', coordinatorEmail: 'k.ramesh@college.edu', capacity: 100, memberCount: 65, active: true },
    { clubId: 3, id: 3, name: 'Cyber Security & Ethical Hacking Club', clubName: 'Cyber Security & Ethical Hacking Club', category: 'Technical', icon: '🛡️', categoryIcon: '🛡️', description: 'Penetration testing, network defense, digital forensics, CTF challenges, and secure coding practices.', coordinatorName: 'Dr. V. Priya', coordinatorEmail: 'v.priya@college.edu', capacity: 90, memberCount: 52, active: true },
    { clubId: 4, id: 4, name: 'Web & Mobile App Development Club', clubName: 'Web & Mobile App Development Club', category: 'Technical', icon: '📱', categoryIcon: '📱', description: 'Full-stack development, modern web architectures, React, Flutter, REST APIs, and cloud deployments.', coordinatorName: 'Prof. A. Kumar', coordinatorEmail: 'a.kumar@college.edu', capacity: 100, memberCount: 70, active: true },
    { clubId: 5, id: 5, name: 'Cloud Computing & DevOps Society', clubName: 'Cloud Computing & DevOps Society', category: 'Technical', icon: '☁️', categoryIcon: '☁️', description: 'Containerization, CI/CD pipelines, Kubernetes, AWS, Google Cloud, and site reliability engineering.', coordinatorName: 'Dr. N. Sharma', coordinatorEmail: 'n.sharma@college.edu', capacity: 80, memberCount: 45, active: true },
    { clubId: 6, id: 6, name: 'Data Science & Big Data Analytics Club', clubName: 'Data Science & Big Data Analytics Club', category: 'Technical', icon: '📊', categoryIcon: '📊', description: 'Exploratory data analysis, statistical modeling, Hadoop, Spark, BigQuery, and visual dashboards.', coordinatorName: 'Dr. M. Jayakumar', coordinatorEmail: 'm.jayakumar@college.edu', capacity: 85, memberCount: 58, active: true },
    { clubId: 7, id: 7, name: 'Internet of Things (IoT) & Embedded Systems Club', clubName: 'Internet of Things (IoT) & Embedded Systems Club', category: 'Technical', icon: '🔌', categoryIcon: '🔌', description: 'Arduino, Raspberry Pi, sensor networks, smart automation, and edge hardware interfacing.', coordinatorName: 'Prof. R. Balaji', coordinatorEmail: 'r.balaji@college.edu', capacity: 75, memberCount: 40, active: true },
    { clubId: 8, id: 8, name: 'Literary & Debating Society', clubName: 'Literary & Debating Society', category: 'Literary', icon: '📚', categoryIcon: '📚', description: 'Parliamentary debates, elocution, creative writing, poetry recitals, and critical rhetoric.', coordinatorName: 'Dr. E. Elizabeth', coordinatorEmail: 'e.elizabeth@college.edu', capacity: 100, memberCount: 60, active: true },
    { clubId: 9, id: 9, name: 'Dramatics & Theatre Club', clubName: 'Dramatics & Theatre Club', category: 'Cultural', icon: '🎭', categoryIcon: '🎭', description: 'Stage plays, street theatre (nukkad natak), scripting, voice modulation, and stagecraft.', coordinatorName: 'Prof. S. Manoharan', coordinatorEmail: 's.manoharan@college.edu', capacity: 70, memberCount: 38, active: true },
    { clubId: 10, id: 10, name: 'Music & Acoustic Band Club', clubName: 'Music & Acoustic Band Club', category: 'Cultural', icon: '🎵', categoryIcon: '🎵', description: 'Classical vocalists, Western instrumentalists, campus rock bands, and digital music production.', coordinatorName: 'Prof. T. Swaminathan', coordinatorEmail: 't.swaminathan@college.edu', capacity: 80, memberCount: 55, active: true },
    { clubId: 11, id: 11, name: 'Fine Arts & Painting Club', clubName: 'Fine Arts & Painting Club', category: 'Creative Arts', icon: '🎨', categoryIcon: '🎨', description: 'Sketching, acrylics, oil painting, digital illustration, sculpturing, and campus murals.', coordinatorName: 'Dr. S. Revathi', coordinatorEmail: 's.revathi@college.edu', capacity: 65, memberCount: 35, active: true },
    { clubId: 12, id: 12, name: 'Photography & Cinematography Club', clubName: 'Photography & Cinematography Club', category: 'Media', icon: '📷', categoryIcon: '📷', description: 'Digital photography, portraiture, short filmmaking, post-processing, and drone cinematography.', coordinatorName: 'Prof. G. Karthik', coordinatorEmail: 'g.karthik@college.edu', capacity: 75, memberCount: 48, active: true },
    { clubId: 13, id: 13, name: 'Dance & Choreography Club', clubName: 'Dance & Choreography Club', category: 'Cultural', icon: '💃', categoryIcon: '💃', description: 'Classical Bharatanatyam, contemporary, hip-hop, western freestyle, and group formations.', coordinatorName: 'Prof. M. Divya', coordinatorEmail: 'm.divya@college.edu', capacity: 80, memberCount: 50, active: true },
    { clubId: 14, id: 14, name: 'Sports & Athletics Association', clubName: 'Sports & Athletics Association', category: 'Sports', icon: '⚽', categoryIcon: '⚽', description: 'Football, basketball, cricket, badminton, track athletics, and tournament organizing.', coordinatorName: 'Dr. P. Selvan', coordinatorEmail: 'p.selvan@college.edu', capacity: 150, memberCount: 110, active: true },
    { clubId: 15, id: 15, name: 'Yoga & Wellness Club', clubName: 'Yoga & Wellness Club', category: 'Health & Wellness', icon: '🧘', categoryIcon: '🧘', description: 'Hatha yoga, pranayama breathing, mindfulness meditation, posture alignment, and stress relief.', coordinatorName: 'Prof. H. Lakshmi', coordinatorEmail: 'h.lakshmi@college.edu', capacity: 90, memberCount: 42, active: true },
    { clubId: 16, id: 16, name: 'National Service Scheme (NSS)', clubName: 'National Service Scheme (NSS)', category: 'Social Service', icon: '🤝', categoryIcon: '🤝', description: 'Community empowerment, blood donation drives, literacy outreach, and rural village development camps.', coordinatorName: 'Dr. C. Sundar', coordinatorEmail: 'c.sundar@college.edu', capacity: 150, memberCount: 125, active: true },
    { clubId: 17, id: 17, name: 'Youth Red Cross (YRC)', clubName: 'Youth Red Cross (YRC)', category: 'Social Service', icon: '🏥', categoryIcon: '🏥', description: 'First aid certification, disaster management response, health awareness, and humanitarian assistance.', coordinatorName: 'Dr. K. Anitha', coordinatorEmail: 'k.anitha@college.edu', capacity: 120, memberCount: 80, active: true },
    { clubId: 18, id: 18, name: 'Environmental & Nature Conservation Club', clubName: 'Environmental & Nature Conservation Club', category: 'Environmental', icon: '🌿', categoryIcon: '🌿', description: 'Tree plantation drives, waste recycling audits, biodiversity conservation, and eco-sustainability.', coordinatorName: 'Prof. D. Vasudevan', coordinatorEmail: 'd.vasudevan@college.edu', capacity: 80, memberCount: 45, active: true },
    { clubId: 19, id: 19, name: 'Mathematics & Problem Solving Club', clubName: 'Mathematics & Problem Solving Club', category: 'Academic', icon: '📐', categoryIcon: '📐', description: 'Discrete mathematics, combinatorics, puzzle solving, cryptography, and quantitative aptitude.', coordinatorName: 'Dr. G. Srinivasan', coordinatorEmail: 'g.srinivasan@college.edu', capacity: 70, memberCount: 36, active: true },
    { clubId: 20, id: 20, name: 'Entrepreneurship & Innovation Cell (E-Cell)', clubName: 'Entrepreneurship & Innovation Cell (E-Cell)', category: 'Entrepreneurship', icon: '🚀', categoryIcon: '🚀', description: 'Startup incubation, business model canvas, venture pitching, mentor networking, and seed grants.', coordinatorName: 'Prof. R. Venkatesh', coordinatorEmail: 'r.venkatesh@college.edu', capacity: 100, memberCount: 68, active: true },
    { clubId: 21, id: 21, name: 'Quantum Computing & Advanced Physics Club', clubName: 'Quantum Computing & Advanced Physics Club', category: 'Innovation', icon: '⚛️', categoryIcon: '⚛️', description: 'Qubits, quantum algorithms, Qiskit circuits, quantum cryptography, and quantum entanglement.', coordinatorName: 'Dr. A. Ramanathan', coordinatorEmail: 'a.ramanathan@college.edu', capacity: 50, memberCount: 28, active: true },
    { clubId: 22, id: 22, name: 'Blockchain & Web3 Developers Guild', clubName: 'Blockchain & Web3 Developers Guild', category: 'Innovation', icon: '⛓️', categoryIcon: '⛓️', description: 'Smart contracts, Solidity, decentralized finance (DeFi), Ethereum, and consensus protocols.', coordinatorName: 'Prof. S. Vignesh', coordinatorEmail: 's.vignesh@college.edu', capacity: 75, memberCount: 44, active: true },
    { clubId: 23, id: 23, name: 'AR/VR & Metaverse Experience Lab', clubName: 'AR/VR & Metaverse Experience Lab', category: 'Innovation', icon: '🥽', categoryIcon: '🥽', description: 'Spatial computing, Unity 3D, Unreal Engine, Oculus headset development, and virtual simulations.', coordinatorName: 'Dr. P. Malathi', coordinatorEmail: 'p.malathi@college.edu', capacity: 60, memberCount: 35, active: true },
    { clubId: 24, id: 24, name: 'Space Tech & Astronomy Guild', clubName: 'Space Tech & Astronomy Guild', category: 'Science', icon: '🔭', categoryIcon: '🔭', description: 'Telescopic sky observation, satellite orbits, CanSat modeling, and astrophysics seminars.', coordinatorName: 'Dr. K. Chandrasekar', coordinatorEmail: 'k.chandrasekar@college.edu', capacity: 70, memberCount: 41, active: true },
    { clubId: 25, id: 25, name: 'Bio-Informatics & Genomics Research Club', clubName: 'Bio-Informatics & Genomics Research Club', category: 'Research', icon: '🧬', categoryIcon: '🧬', description: 'Computational biology, DNA sequence alignment, AlphaFold protein structures, and Biopython.', coordinatorName: 'Dr. U. Banumathi', coordinatorEmail: 'u.banumathi@college.edu', capacity: 50, memberCount: 25, active: true },
    { clubId: 26, id: 26, name: 'Game Design & Interactive Media Studio', clubName: 'Game Design & Interactive Media Studio', category: 'Gaming', icon: '🎮', categoryIcon: '🎮', description: 'Indie game development, 2D/3D physics engines, character animation, and level mechanics.', coordinatorName: 'Prof. J. Vijay', coordinatorEmail: 'j.vijay@college.edu', capacity: 80, memberCount: 52, active: true },
    { clubId: 27, id: 27, name: 'Drone Technology & UAV Research Club', clubName: 'Drone Technology & UAV Research Club', category: 'Aerospace', icon: '🚁', categoryIcon: '🚁', description: 'Quadcopter assembly, flight controllers, autopilot navigation, FPV racing, and aerial survey.', coordinatorName: 'Dr. R. Natarajan', coordinatorEmail: 'r.natarajan@college.edu', capacity: 60, memberCount: 34, active: true },
    { clubId: 28, id: 28, name: 'Fintech & Algorithmic Trading Society', clubName: 'Fintech & Algorithmic Trading Society', category: 'Finance', icon: '📈', categoryIcon: '📈', description: 'Automated trading bots, backtesting quantitative models, portfolio risk, and payment gateways.', coordinatorName: 'Prof. S. Deepak', coordinatorEmail: 's.deepak@college.edu', capacity: 65, memberCount: 39, active: true },
    { clubId: 29, id: 29, name: 'Social Media & Digital Content Creators Club', clubName: 'Social Media & Digital Content Creators Club', category: 'Media', icon: '🎙️', categoryIcon: '🎙️', description: 'Podcasting, viral reels production, brand identity, audio editing, and audience analytics.', coordinatorName: 'Prof. A. Sneha', coordinatorEmail: 'a.sneha@college.edu', capacity: 85, memberCount: 56, active: true },
    { clubId: 30, id: 30, name: 'Model United Nations (MUN) & Geopolitics Society', clubName: 'Model United Nations (MUN) & Geopolitics Society', category: 'Leadership', icon: '🌐', categoryIcon: '🌐', description: 'Diplomatic simulation, resolution drafting, UN committees, crisis debate, and foreign affairs.', coordinatorName: 'Dr. F. George', coordinatorEmail: 'f.george@college.edu', capacity: 90, memberCount: 62, active: true }
  ];

  // Define 8 default scheduled events
  const defaultEvents = [
    { eventId: 101, id: 101, title: 'Annual 24-Hour University Hackathon 2026', clubName: 'Coding & Open Source Club', clubId: 1, description: 'Build cutting-edge full-stack and AI software solutions. Cash prizes, mentor support, and industry judging.', eventDate: '2026-10-25 09:00', venue: 'Main Auditorium & Computing Center', capacity: 150, registeredCount: 85, status: 'OPEN' },
    { eventId: 102, id: 102, title: 'Autonomous Robotics & AI Expo 2026', clubName: 'Artificial Intelligence & Robotics Club', clubId: 2, description: 'Live demonstrations of computer vision robots, obstacle-avoiding rovers, and generative AI models.', eventDate: '2026-10-28 10:30', venue: 'Robotics Innovation Lab - Block C', capacity: 100, registeredCount: 62, status: 'OPEN' },
    { eventId: 103, id: 103, title: 'Campus Cyber Defense CTF Challenge', clubName: 'Cyber Security & Ethical Hacking Club', clubId: 3, description: 'Inter-collegiate ethical hacking challenge covering cryptography, reverse engineering, and web exploits.', eventDate: '2026-11-05 14:00', venue: 'Cyber Security Cyber-Range Lab', capacity: 80, registeredCount: 45, status: 'OPEN' },
    { eventId: 104, id: 104, title: 'National Parliamentary Debate Championship', clubName: 'Literary & Debating Society', clubId: 8, description: 'Oxford-style debating on global affairs, AI ethics, and economic policy.', eventDate: '2026-11-12 10:00', venue: 'Senate Hall, Admin Building', capacity: 120, registeredCount: 78, status: 'OPEN' },
    { eventId: 105, id: 105, title: 'Campus Life Photo Walk & Film Screening', clubName: 'Photography & Cinematography Club', clubId: 12, description: 'Guided photography walk around heritage campus followed by screening of student short films.', eventDate: '2026-11-18 15:30', venue: 'Open Air Theatre & Media Studio', capacity: 90, registeredCount: 54, status: 'OPEN' },
    { eventId: 106, id: 106, title: 'Annual Inter-Department Athletics Meet', clubName: 'Sports & Athletics Association', clubId: 14, description: 'Sprint races, relay, long jump, badminton, and basketball tournaments across all university branches.', eventDate: '2026-11-25 08:00', venue: 'University Sports Complex Stadium', capacity: 200, registeredCount: 130, status: 'OPEN' },
    { eventId: 107, id: 107, title: 'Acoustic Unplugged & Rock Band Night', clubName: 'Music & Acoustic Band Club', clubId: 10, description: 'Live instrumental, classical fusion, and acoustic band performances under the stars.', eventDate: '2026-12-02 18:00', venue: 'Campus Amphitheatre', capacity: 250, registeredCount: 165, status: 'OPEN' },
    { eventId: 108, id: 108, title: 'E-Summit 2026: Venture Pitch Showcase', clubName: 'Entrepreneurship & Innovation Cell (E-Cell)', clubId: 20, description: 'Student founders pitch startup ideas to angel investors and venture capitalists with seed grants.', eventDate: '2026-12-10 11:00', venue: 'Convention Center Hall A', capacity: 120, registeredCount: 90, status: 'OPEN' }
  ];

  // Specific arrays FIRST before generic /me matching
  if (endpoint.includes('notification')) {
    if (endpoint.includes('/read-all') || endpoint.includes('/read')) {
      return { success: true, message: 'Notification marked as read' };
    }
    return {
      success: true,
      data: [
        { notificationId: 1, id: 1, title: 'Welcome to Campus Clubs', message: 'Explore 30+ clubs and register for upcoming university workshops.', createdAt: 'Just now', isRead: false },
        { notificationId: 2, id: 2, title: 'Hackathon 2026 Registration Open', message: 'Coding Club opened 150 slots for the annual campus hackathon.', createdAt: '2 hours ago', isRead: false },
        { notificationId: 3, id: 3, title: 'Robotics Workshop Alert', message: 'AI & Robotics Club scheduled an autonomous bots demo session.', createdAt: '1 day ago', isRead: true }
      ]
    };
  }

  if (endpoint.includes('my-clubs') || endpoint.includes('myClubs') || endpoint.includes('/me/clubs')) {
    const myClubIds = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1, 2]');
    return { success: true, data: defaultClubs.filter(c => myClubIds.includes(c.clubId || c.id)) };
  }

  if (endpoint.includes('my-events') || endpoint.includes('myEvents') || endpoint.includes('/me/events')) {
    const myEvtIds = JSON.parse(localStorage.getItem('ccms_my_events') || '[101]');
    return { success: true, data: defaultEvents.filter(e => myEvtIds.includes(e.eventId || e.id)) };
  }

  if (endpoint.includes('logout')) {
    session.clear();
    return { success: true, message: 'Logged out' };
  }

  if (endpoint.endsWith('/me') || endpoint.endsWith('/auth/me') || endpoint === '/api/students/me' || (endpoint.includes('/me') && !endpoint.includes('/me/'))) {
    const s = session.get() || {};
    return {
      success: true,
      data: {
        userId: s.userId || 101,
        id: s.userId || 101,
        username: s.username || 'student01',
        fullName: s.fullName || 'Rahul Kumar',
        email: s.email || `${s.username || 'student01'}@college.edu`,
        rollNumber: s.rollNumber || '26MCA001',
        department: s.department || 'Computer Applications',
        course: s.course || 'MCA',
        year: s.year || 1,
        role: s.role || 'STUDENT'
      }
    };
  }

  if (endpoint.includes('clubs')) {
    if (endpoint.includes('/join') && method === 'POST') {
      const myClubs = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1, 2]');
      const clubId = parseInt(endpoint.split('/')[3] || '1', 10);
      if (!myClubs.includes(clubId)) myClubs.push(clubId);
      localStorage.setItem('ccms_my_clubs', JSON.stringify(myClubs));
      return { success: true, message: 'Successfully joined club!' };
    }
    if (endpoint.includes('/leave') || (endpoint.includes('/join') && method === 'DELETE')) {
      let myClubs = JSON.parse(localStorage.getItem('ccms_my_clubs') || '[1, 2]');
      const clubId = parseInt(endpoint.split('/')[3] || '1', 10);
      myClubs = myClubs.filter(id => id !== clubId);
      localStorage.setItem('ccms_my_clubs', JSON.stringify(myClubs));
      return { success: true, message: 'Successfully left club!' };
    }
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
    return { success: true, data: defaultEvents };
  }

  if (endpoint.includes('stats')) {
    return {
      success: true,
      data: {
        totalStudents: 142,
        activeClubs: 30,
        upcomingEvents: 8,
        totalRegistrations: 187,
        totalMemberships: 215,
        pendingApprovals: 4
      }
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
