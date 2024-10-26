package app.persistence;

import app.controllers.DatabaseController;
import app.entities.User;
import app.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    // Login method without BCrypt verification; verification is done in UserController
    public static User login(String userName, DatabaseController databaseController) throws DatabaseException {
        String sql = "SELECT user_id, password, role FROM public.\"users\" WHERE username=?";

        try (
                Connection connection = databaseController.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String hashedPassword = rs.getString("password");
                String role = rs.getString("role");

                return new User(id, userName, hashedPassword, role);
            } else {
                throw new DatabaseException("User not found in database");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error occurred. Please try again later.");
        }
    }

    // Method to create a new user with hashed password
    public static void createUser(String userName, String hashedPassword, DatabaseController databaseController) throws DatabaseException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (
                Connection connection = databaseController.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userName);
            ps.setString(2, hashedPassword);
            ps.setString(3, "kunde");

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Error creating new user");
            }
        } catch (SQLException e) {
            String msg = "An error occurred. Please try again";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "Username already exists. Choose a different one";
            }
            throw new DatabaseException(msg);
        }
    }

}
