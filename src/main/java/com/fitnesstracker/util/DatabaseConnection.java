package com.fitnesstracker.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection utility class for managing MySQL connections.
 */
public class DatabaseConnection {
    private static final Properties properties = new Properties();
    private static String url;
    private static String username;
    private static String password;
    
    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
                url = properties.getProperty("db.url");
                username = properties.getProperty("db.username");
                password = properties.getProperty("db.password");
                String driver = properties.getProperty("db.driver");
                if (driver != null) {
                    Class.forName(driver);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
        }
    }

    /**
     * Get a database connection using properties from db.properties.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Configure database connection programmatically.
     */
    public static void configure(String dbUrl, String dbUsername, String dbPassword) {
        url = dbUrl;
        username = dbUsername;
        password = dbPassword;
    }

    /**
     * Close a database connection safely.
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
