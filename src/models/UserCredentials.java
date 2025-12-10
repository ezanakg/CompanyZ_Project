package src.models;

/**
 * UserCredentials encapsulates authentication data.
 * Separates credential validation from authentication logic.
 */
public class UserCredentials {
    private String username;
    private String password;
    private String role;

    public UserCredentials(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }
    public boolean isEmployee() { return "EMPLOYEE".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
