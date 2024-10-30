package app.entities;

public class CustomerDetails {
    private int userId;
    private String username;
    private String role;
    private double balance; // Du kan tilføje balance, hvis du vil holde styr på det også.

    // Konstruktor
    public CustomerDetails(int userId, String username, String role, double balance) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.balance = balance;
    }

    // Getters og Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
