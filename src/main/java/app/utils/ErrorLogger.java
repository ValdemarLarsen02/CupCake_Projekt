package app.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import app.controllers.DatabaseController;

public class ErrorLogger {

    // Gemmer vores fejl i databasen. Bruges i vores kodning process så vi nememre kan se fejl mm
    public static void logError(Exception ex) {
        DatabaseController db = new DatabaseController();
        Connection connection = db.getConnection();

        try {
            String query = "INSERT INTO error_logs (error_message, stack_trace, timestamp) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            // Sætter fejlbesked og stack trace som tekst
            statement.setString(1, ex.getMessage());
            statement.setString(2, getStackTraceAsString(ex));
            statement.setTimestamp(3, Timestamp.from(Instant.now()));  // Tidspunkt.

            statement.executeUpdate();
            System.out.println("Fejl logget til databasen.");
        } catch (SQLException e) {
            e.printStackTrace();  // Hvis fejlen ikke gemmes printes den til console..
        } finally {
            db.closeConnection();
        }
    }

    // Hjælpemetode til at konvertere stack trace til en string
    private static String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
