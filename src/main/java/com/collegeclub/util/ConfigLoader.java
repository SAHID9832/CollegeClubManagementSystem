package com.collegeclub.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration properties from classpath application.properties
 * and merges with system environment variables for secrets.
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception e) {
            System.err.println("Warning: Unable to load application.properties: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getDbUrl() {
        String prop = System.getProperty("CLUB_DB_URL");
        if (prop != null && !prop.trim().isEmpty()) {
            return prop.trim();
        }
        String env = System.getenv("CLUB_DB_URL");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        String raw = properties.getProperty("db.url", "jdbc:h2:${user.home}/collegeclub-data/collegeclub;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=MySQL");
        String userHome = System.getProperty("user.home", ".");
        return raw.replace("${user.home}", userHome.replace("\\", "/"));
    }

    public static String getDbUser() {
        String env = System.getenv("CLUB_DB_USER");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return properties.getProperty("db.user", "sa");
    }

    public static String getDbPassword() {
        String env = System.getenv("CLUB_DB_PASSWORD");
        if (env != null) {
            return env;
        }
        return properties.getProperty("db.password", "");
    }

    public static String getDbDriver() {
        String url = getDbUrl();
        if (url.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return properties.getProperty("db.driver", "org.h2.Driver");
    }

    public static String getAdminUsername() {
        String env = System.getenv("CLUB_ADMIN_USERNAME");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return properties.getProperty("app.admin.username", "admin");
    }

    public static String getAdminPassword() {
        String env = System.getenv("CLUB_ADMIN_PASSWORD");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        return properties.getProperty("app.admin.defaultPassword", "Admin@123");
    }

    public static boolean isDemoMode() {
        return Boolean.parseBoolean(properties.getProperty("app.demoMode", "true"));
    }

    public static int getPbkdf2Iterations() {
        String env = System.getenv("CLUB_PBKDF2_ITERATIONS");
        if (env != null) {
            try { return Integer.parseInt(env.trim()); } catch (Exception ignored) {}
        }
        String prop = properties.getProperty("security.pbkdf2.iterations", "600000");
        try { return Integer.parseInt(prop.trim()); } catch (Exception ignored) {}
        return 600_000;
    }
}
