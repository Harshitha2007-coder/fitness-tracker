package com.fitnesstracker.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility for MySQL.
 */
public class DatabaseConnection {
    private static final Properties properties = new Properties();
    private static String url;
    private static String username;
    private static String password;
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
                url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/fitness_tracker");
                username = properties.getProperty("db.username", "root");
                password = properties.getProperty("db.password", "");
            } else {
                // Default configuration
                url = "jdbc:mysql://localhost:3306/fitness_tracker";
                username = "root";
                password = "";
            }
            initialized = true;
        } catch (IOException e) {
            // Use defaults if config file not found
            url = "jdbc:mysql://localhost:3306/fitness_tracker";
            username = "root";
            password = "";
            initialized = true;
        }
    }

    /**
     * Configure database connection programmatically.
     */
    public static void configure(String dbUrl, String dbUsername, String dbPassword) {
        url = dbUrl;
        username = dbUsername;
        password = dbPassword;
        initialized = true;
    }

    /**
     * Get a database connection.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            loadConfiguration();
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get the configured database URL.
     */
    public static String getUrl() {
        return url;
    }

    /**
     * Close a connection safely.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
