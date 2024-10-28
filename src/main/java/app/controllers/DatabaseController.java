package app.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseController {
    private Connection connection;

    // Constructor
    public DatabaseController() {
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            // Opretter forbindelse til databasen
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Forbindelse til databasen blev oprettet.");
        } catch (SQLException e) {
            System.err.println("Kunne ikke oprette forbindelse til databasen: " + e.getMessage());
        }
    }

    // Returnerer forbindelsen
    public Connection getConnection() {
        return connection;
    }

    // Lukker forbindelsen
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Forbindelsen til databasen blev lukket.");
            }
        } catch (SQLException e) {
            System.err.println("Fejl ved lukning af forbindelse: " + e.getMessage());
        }
    }

    // Genetablerer forbindelse (hvis nødvendigt)
    public void reconnect() {
        try {
            if (connection == null || connection.isClosed()) {
                Dotenv dotenv = Dotenv.load();
                String dbUrl = dotenv.get("DB_URL");
                String dbUsername = dotenv.get("DB_USERNAME");
                String dbPassword = dotenv.get("DB_PASSWORD");

                connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                System.out.println("Forbindelse til databasen blev genetableret.");
            }
        } catch (SQLException e) {
            System.err.println("Kunne ikke genetablere forbindelse til databasen: " + e.getMessage());
        }
    }

    // Opdaterer kundens saldo i databasen
    public void opdaterSaldo(int kundeId, double beloeb) {
        String sql = "UPDATE customers SET balance = balance + ? WHERE customer_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, beloeb);
            preparedStatement.setInt(2, kundeId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Saldoen blev opdateret for kunde med ID: " + kundeId);
            } else {
                System.out.println("Ingen kunde fundet med det givne ID.");
            }
        } catch (SQLException e) {
            System.err.println("Fejl ved opdatering af saldo: " + e.getMessage());
        }
    }
}
