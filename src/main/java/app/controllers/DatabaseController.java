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
        // Læs miljøvariabler fra .env filen for at beskytte databaseoplysninger
        Dotenv dotenv = Dotenv.load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            // Opret forbindelse til databasen
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Forbindelse til databasen blev oprettet.");
        } catch (SQLException e) {
            // Fejlhåndtering ved oprettelse af forbindelse
            System.err.println("Kunne ikke oprette forbindelse til databasen: " + e.getMessage());
        }
    }

    // Returner forbindelsen
    public Connection getConnection() {
        return connection;
    }

    // Luk forbindelsen
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

    // Genetabler forbindelse (hvis nødvendigt)
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

    // Opdater kundens saldo i databasen
    public void opdaterSaldo(int kundeId, double beloeb) {
        String sql = "UPDATE kunder SET saldo = saldo + ? WHERE id = ?";

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
