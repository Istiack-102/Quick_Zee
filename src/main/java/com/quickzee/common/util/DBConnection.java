package com.quickzee.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection utility class
 * Handles database connection setup and provides connection instances
 */
public class DBConnection {

    // Database credentials - CHANGE THESE TO MATCH YOUR SETUP
    private static final String URL = "jdbc:mysql://localhost:3306/Quick_Zee";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "istiack123@";  // ← CHANGE THIS!

    // JDBC Driver class name
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Static block to load the MySQL JDBC driver
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("✅ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Test the database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close a connection safely
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error closing connection: " + e.getMessage());
            }
        }
    }

    // Optional: Print connection info (for debugging)
    public static void printConnectionInfo() {
        System.out.println("=== Database Connection Info ===");
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        System.out.println("Driver: " + DRIVER);
        System.out.println("==============================");
    }
}