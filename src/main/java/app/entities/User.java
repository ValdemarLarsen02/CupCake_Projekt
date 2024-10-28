package app.entities;

import java.sql.SQLOutput;

public class User {
    private int userId;
    private String userName;
    private String hashedPassword;  // Renamed for clarity
    private String role;

    public User(int userId, String userName, String hashedPassword, String role) {
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }



    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role='" + role + '\'' +  // Avoid displaying hashedPassword
                '}';
    }

    public String getPassword() {
        System.out.println(hashedPassword);
        return hashedPassword;
    }
}
