package app.entities;

import java.sql.Connection;

public class Customer {
    private String name;
    private String email;
    private int phone;
    private String role;
    private int customerId;
    private String password;
    private double balance;
    private Connection connection;

    // Constructor
    public Customer(String name, String email, int phone, String role, int customerId, String password, double balance, Connection connection) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.customerId = customerId;
        this.password = password;
        this.balance = balance;
        this.connection = connection;
    }

    // Getters og setters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
