package src.repositories;

import src.models.UserCredentials;

/**
 * MockAuthRepository provides demo authentication for testing.
 * Useful when database is not available.
 * Demo Credentials:
 * - Username: admin, Password: admin123 → ADMIN role
 * - Username: employee, Password: emp123 → EMPLOYEE role
 */
public class MockAuthRepository implements IAuthRepository {

    @Override
    public UserCredentials validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }

        // Demo credentials for testing
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            return new UserCredentials(username, password, "ADMIN");
        }
        
        if ("employee".equalsIgnoreCase(username) && "emp123".equals(password)) {
            return new UserCredentials(username, password, "EMPLOYEE");
        }
        
        // Additional test credentials
        if ("demo".equalsIgnoreCase(username) && "demo123".equals(password)) {
            return new UserCredentials(username, password, "ADMIN");
        }
        
        return null;
    }
}
