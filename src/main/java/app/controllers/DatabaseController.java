package app.controllers;

import app.utils.ErrorLogger;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {
    private Connection connection;


    public DatabaseController() {
        // Læser vores oplysninger fra .env filen | Fjernet fra github så kode mm ikke bliver leaket.
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Forbindelse til databasen blev oprettet.");
        } catch (SQLException e) {
            ErrorLogger.logError(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Forbindelsen til databasen blev lukket.");
            }
        } catch (SQLException e) {
            ErrorLogger.logError(e);
        }
    }
}
