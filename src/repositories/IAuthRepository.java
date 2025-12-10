package src.repositories;

import src.models.UserCredentials;

/**
 * IAuthRepository defines the contract for authentication operations.
 * Follows Interface Segregation Principle - clients depend only on auth methods.
 */
public interface IAuthRepository {
    /**
     * Validates user credentials and returns their role.
     * @param username the user's username
     * @param password the user's password
     * @return UserCredentials if valid, null otherwise
     */
    UserCredentials validateLogin(String username, String password);
}
