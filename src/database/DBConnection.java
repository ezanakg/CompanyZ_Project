package src.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection provides database connectivity.
 * Encapsulates database configuration with proper error handling.
 * Single Responsibility: manages database connections only.
 * 
 * Future enhancement: Implement connection pooling (HikariCP, C3P0)
 * for better resource management in production environments.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/employeeData";
    private static final String USER = "XXX"; // CHANGE THIS
    private static final String PASSWORD = "XXX"; // CHANGE THIS
    
    private static final int CONNECTION_TIMEOUT = 5000;

    private DBConnection() {
        // Private constructor prevents instantiation
        // Enforces static method usage pattern
    }

    /**
     * Gets a database connection with configured timeout.
     * @return Connection to the employee database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setNetworkTimeout(null, CONNECTION_TIMEOUT);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed. Verify credentials and MySQL server is running.");
            throw new SQLException("Failed to establish database connection: " + e.getMessage(), e);
        }
    }

    /**
     * Validates database connectivity.
     * Useful for startup checks and monitoring.
     * @return true if database is accessible
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Database validation failed: " + e.getMessage());
            return false;
        }
    }
}