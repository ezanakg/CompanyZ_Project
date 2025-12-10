package src.users;

import src.models.UserCredentials;

/**
 * Abstract User class defining common user behavior.
 * Enables polymorphic treatment of different user types.
 * Follows Open/Closed Principle: open for extension, closed for modification.
 */
public abstract class User {
    protected String username;
    protected String role;

    public User(UserCredentials credentials) {
        this.username = credentials.getUsername();
        this.role = credentials.getRole();
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }

    /**
     * Abstract method for role-specific dashboard display.
     * Each subclass implements its own dashboard logic.
     */
    public abstract void displayDashboard();

    /**
     * Template method for common user actions.
     */
    public void logout() {
        System.out.println(username + " has logged out.");
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
