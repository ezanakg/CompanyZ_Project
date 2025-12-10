package src.services;

import src.models.UserCredentials;
import src.repositories.IAuthRepository;

/**
 * AuthService implements authentication business logic.
 * Single Responsibility: handles authentication validation.
 * Depends on abstraction (IAuthRepository) not concrete implementation.
 */
public class AuthService {
    private final IAuthRepository authRepository;

    public AuthService(IAuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Validates login credentials and returns user credentials.
     * @param username the user's username
     * @param password the user's password
     * @return UserCredentials if valid, null otherwise
     */
    public UserCredentials login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        return authRepository.validateLogin(username, password);
    }

    /**
     * Checks if a user role is an administrator.
     * @param credentials the user credentials
     * @return true if user is an admin
     */
    public boolean isAdmin(UserCredentials credentials) {
        return credentials != null && credentials.isAdmin();
    }

    /**
     * Checks if a user role is a regular employee.
     * @param credentials the user credentials
     * @return true if user is an employee
     */
    public boolean isEmployee(UserCredentials credentials) {
        return credentials != null && credentials.isEmployee();
    }
}
