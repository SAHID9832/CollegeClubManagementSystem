-- ====================================================================
-- College Club Management System Database Schema
-- Compatible with both H2 Database Engine (embedded) and MySQL 8.x
-- Safe to re-run (idempotent), strictly preserves existing data
-- ====================================================================

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at VARCHAR(30) NOT NULL
);

-- Students Profile Table (extends users)
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    course VARCHAR(50) NOT NULL DEFAULT 'MCA',
    study_year INT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Clubs Table
CREATE TABLE IF NOT EXISTS clubs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    category VARCHAR(50) NOT NULL DEFAULT 'General',
    coordinator VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 100,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    objectives VARCHAR(1000),
    activities VARCHAR(1000),
    icon VARCHAR(50) DEFAULT '🛡️',
    created_at VARCHAR(30)
);

-- Club Memberships Table
CREATE TABLE IF NOT EXISTS club_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    club_id INT NOT NULL,
    user_id INT NOT NULL,
    joined_at VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uk_club_user UNIQUE (club_id, user_id),
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Events Table
CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    club_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    event_date VARCHAR(30) NOT NULL,
    venue VARCHAR(150) NOT NULL,
    capacity INT NOT NULL DEFAULT 50,
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
);

-- Event Registrations Table
CREATE TABLE IF NOT EXISTS event_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    registered_at VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    CONSTRAINT uk_event_user UNIQUE (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at VARCHAR(30) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Seed Core Clubs matching reference design (if not present)
INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 1, 'Coding & Development', 'Build real-world software, master DSA, and innovate open-source projects.', 'Technical', 'Dr. S. Arul', 120, TRUE, 'Fostering coding proficiency', 'Weekly hackathons, DSA sprints', '</>', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 1 OR name = 'Coding & Development');

INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 2, 'AI & ML Club', 'Explore machine learning models, neural networks, and computer vision.', 'Technical', 'Prof. K. Ramesh', 98, TRUE, 'AI Research and Bot development', 'AI Paper reviews, Kaggle sprints', '🧠', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 2 OR name = 'AI & ML Club');

INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 3, 'Photography Club', 'Capture campus moments, master DSLR mechanics, and visual storytelling.', 'Cultural', 'Prof. G. Karthik', 75, TRUE, 'Visual arts & cinematography', 'Campus photowalks, LightRoom clinics', '📷', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 3 OR name = 'Photography Club');

INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 4, 'Music Club', 'Acoustic jams, classical vocals, band performances, and studio recordings.', 'Cultural', 'Prof. T. Swaminathan', 110, TRUE, 'Musical composition & ensemble', 'Unplugged nights, Band wars', '🎵', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 4 OR name = 'Music Club');

INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 5, 'Sports Club', 'Inter-department athletics, football leagues, basketball, and fitness.', 'Sports', 'Dr. P. Selvan', 95, TRUE, 'Athletic discipline & fitness', 'Sports tournaments, Marathon runs', '🏀', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 5 OR name = 'Sports Club');

INSERT INTO clubs (id, name, description, category, coordinator, capacity, active, objectives, activities, icon, created_at)
SELECT 6, 'Entrepreneurship Club', 'Startup incubation, venture capital pitches, and innovation bootcamps.', 'Academic', 'Prof. R. Venkatesh', 80, TRUE, 'Startup leadership & ventures', 'E-Summit, Pitch nights', '💡', '2026-10-12'
WHERE NOT EXISTS (SELECT 1 FROM clubs WHERE id = 6 OR name = 'Entrepreneurship Club');

-- Seed Core Events matching reference design (if not present)
INSERT INTO events (id, club_id, title, description, event_date, venue, capacity)
SELECT 1, 1, 'Campus Coding Contest', 'High-intensity algorithmic programming sprint with live leaderboard and prizes.', '2026-11-15 10:00', 'Computer Lab 1', 100
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 1 OR title = 'Campus Coding Contest');

INSERT INTO events (id, club_id, title, description, event_date, venue, capacity)
SELECT 2, 2, 'Inter-Club Quiz', 'University trivia and knowledge battle spanning tech, science, history, and pop culture.', '2026-11-18 14:00', 'Main Auditorium', 150
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 2 OR title = 'Inter-Club Quiz');

INSERT INTO events (id, club_id, title, description, event_date, venue, capacity)
SELECT 3, 4, 'Cultural Fest', 'Grand annual celebration featuring live band performances, dance battles, and theatre.', '2026-11-20 17:00', 'Open Air Theatre', 250
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 3 OR title = 'Cultural Fest');

INSERT INTO events (id, club_id, title, description, event_date, venue, capacity)
SELECT 4, 1, 'Web Design Workshop', 'Hands-on masterclass in modern UI/UX design, CSS animations, and reactive interfaces.', '2026-11-25 11:00', 'Seminar Hall', 80
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 4 OR title = 'Web Design Workshop');

INSERT INTO events (id, club_id, title, description, event_date, venue, capacity)
SELECT 5, 5, 'Gaming Tournament', 'Inter-collegiate esports tournament with CS2, Valorant, and FIFA competitions.', '2026-11-28 17:00', 'Indoor Arena', 120
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 5 OR title = 'Gaming Tournament');

