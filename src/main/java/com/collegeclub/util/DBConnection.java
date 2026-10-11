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

        if (schemaInitialized.compareAndSet(false, true)) {
            initSchema(connection);
        }

        return connection;
    }

    /**
     * Initializes database tables using schema.sql if they do not already exist.
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
                        // Some statements might fail if already created or db dialect differences; log debug
                        if (!e.getMessage().toLowerCase().contains("already exists")) {
                            System.err.println("Schema statement warning: " + e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to execute schema.sql: " + e.getMessage());
        }
    }
}
