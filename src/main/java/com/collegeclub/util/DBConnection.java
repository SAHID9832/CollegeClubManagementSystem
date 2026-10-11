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
            Class.forName(ConfigLoader.getDbDriver());
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found on classpath: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                ConfigLoader.getDbUrl(),
                ConfigLoader.getDbUser(),
                ConfigLoader.getDbPassword()
        );

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

            // Backward-compatible column migrations for existing databases
            String[] migrations = {
                    "ALTER TABLE students ADD COLUMN IF NOT EXISTS course VARCHAR(50) DEFAULT 'MCA'",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS objectives VARCHAR(1000)",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS activities VARCHAR(1000)",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS icon VARCHAR(50) DEFAULT '🛡️'",
                    "ALTER TABLE clubs ADD COLUMN IF NOT EXISTS created_at VARCHAR(30)"
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
