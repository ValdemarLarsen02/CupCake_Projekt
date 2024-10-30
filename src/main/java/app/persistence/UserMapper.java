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

    public static User login(String userName, DatabaseController databaseController) throws DatabaseException {
        String sql = "SELECT user_id, password, role FROM public.\"users\" WHERE username=?";
        Connection connection = databaseController.getConnection();


        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String hashedPassword = rs.getString("password");
                String role = rs.getString("role");

                // Return user object with hashed password for verification in the controller
                return new User(id, userName, hashedPassword, role);
            } else {
                throw new DatabaseException("Bruger ikke fundet."); // User not found
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DatabaseException("Database fejl, prøv igen senere."); // General DB error
        }
    }

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
                throw new DatabaseException("Fejl i oprettelse af bruger.");
            }
        } catch (SQLException e) {
            String msg = "Der skete en fejl, prøv igen.";
            if (e.getMessage().startsWith("FEJL: duplicate key value ")) {
                msg = "Brugernavn findes allerede, prøv et andet.";
            }
            throw new DatabaseException(msg);
        }
    }
}
