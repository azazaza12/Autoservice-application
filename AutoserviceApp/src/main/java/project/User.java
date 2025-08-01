package project;

public class User {
    private String username;
    private String passwordHash;
    private String role;

    public User(String username, String passwordHash,String role ) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;

    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public String getRole() {
        return role;
    }
}