package src.repositories;

import src.models.UserCredentials;
import src.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AuthRepository implements IAuthRepository.
 * Single Responsibility: handles only authentication database operations.
 * Encapsulates SQL logic, exceptions handled internally.
 */
public class AuthRepository implements IAuthRepository {
    private static final String LOGIN_QUERY = "SELECT role FROM users WHERE username = ? AND password = ?";

    @Override
    public UserCredentials validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LOGIN_QUERY)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    return new UserCredentials(username, password, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication query failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
