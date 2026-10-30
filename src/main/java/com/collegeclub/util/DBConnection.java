package com.collegeclub.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Central Database Connection Manager.
 * Provides JDBC Connections using configuration properties and ensures schema
 * initialization on first access.
 */
public class DBConnection {

    private static final AtomicBoolean schemaInitialized = new AtomicBoolean(false);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ignored) {}
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    ConfigLoader.getDbUrl(),
                    ConfigLoader.getDbUser(),
                    ConfigLoader.getDbPassword()
            );
        } catch (SQLException e) {
            if (ConfigLoader.getDbUrl().startsWith("jdbc:mysql:")) {
                System.err.println("Notice: Primary MySQL connection failed (" + e.getMessage() + "). Falling back to embedded H2 database.");
                String userHome = System.getProperty("user.home", ".").replace("\\", "/");
                String fallbackUrl = "jdbc:h2:" + userHome + "/collegeclub-data/collegeclub;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL";
                connection = DriverManager.getConnection(fallbackUrl, "sa", "");
            } else {
                throw e;
            }
        }

        if (!schemaInitialized.get()) {
            synchronized (DBConnection.class) {
                if (!schemaInitialized.get()) {
                    initSchema(connection);
                    schemaInitialized.set(true);
                }
            }
        }

        return connection;
    }

    /**
     * Initializes database tables using schema.sql and runs safe schema updates.
     */
    public static synchronized void initSchema(Connection connection) {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                System.err.println("schema.sql not found on classpath.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }
                sb.append(line).append(" ");
                if (line.endsWith(";")) {
                    String sql = sb.toString().replace(";", "").trim();
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                        if (!msg.contains("already exists") && !msg.contains("duplicate")) {
                            System.err.println("Notice on schema execution: " + e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }

            // Backward-compatible column migrations and data hygiene for existing databases
            String[] migrations = {
                    "ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE",
                    "UPDATE users SET active = TRUE WHERE active IS NULL",
                    "ALTER TABLE event_registrations ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'CONFIRMED'",
                    "ALTER TABLE students ADD COLUMN IF NOT EXISTS course VARCHAR(50) DEFAULT 'MCA'",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS objectives VARCHAR(1000)",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS activities VARCHAR(1000)",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS icon VARCHAR(50) DEFAULT '🛡️'",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS created_at VARCHAR(30)",
                    "DELETE FROM events WHERE title LIKE 'Event %'",
                    "DELETE FROM clubs WHERE name LIKE 'Club %' OR name LIKE 'TinyClub %' OR name LIKE 'EvtClub %' OR name LIKE 'DelTest%' OR name LIKE 'UpdateTest%'",
                    "DELETE FROM clubs WHERE name IN ('Coding Club', 'Coding & Open Source Club', 'Artificial Intelligence & Robotics Club', 'Cyber Security & Ethical Hacking Club', 'Web & Mobile App Development Club', 'Cloud Computing & DevOps Society', 'Data Science & Big Data Analytics Club', 'Internet of Things (IoT) & Embedded Systems Club', 'Literary & Debating Society', 'Dramatics & Theatre Club', 'Music & Acoustic Band Club', 'Fine Arts & Painting Club', 'Photography & Cinematography Club', 'Dance & Choreography Club', 'Sports & Athletics Association', 'Yoga & Wellness Club', 'National Service Scheme (NSS)', 'Youth Red Cross (YRC)', 'Environmental & Nature Conservation Club', 'Mathematics & Problem Solving Club', 'Entrepreneurship & Innovation Cell (E-Cell)', 'Quantum Computing & Advanced Physics Club', 'Blockchain & Web3 Developers Guild', 'AR/VR & Metaverse Experience Lab', 'Space Tech & Astronomy Guild', 'Bio-Informatics & Genomics Research Club', 'Game Design & Interactive Media Studio', 'Drone Technology & UAV Research Club', 'Fintech & Algorithmic Trading Society', 'Social Media & Digital Content Creators Club', 'Model United Nations (MUN) & Geopolitics Society')"
            };

            for (String mig : migrations) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(mig);
                } catch (SQLException ignored) {
                    // Ignored if column already exists or dialect variant
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to execute schema.sql: " + e.getMessage());
        }
    }
}
