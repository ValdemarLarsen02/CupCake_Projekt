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
        connect();
    }

    // Privat metode til at oprette forbindelse
    private void connect() {
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            System.err.println("Kunne ikke oprette forbindelse til databasen: " + e.getMessage());
        }
    }

    // Returnerer forbindelsen og genetablerer hvis nødvendig
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();  // Brug reconnect til at genetablere forbindelsen
            }
        } catch (SQLException e) {
            System.err.println("Fejl ved kontrol af forbindelse: " + e.getMessage());
        }
        return connection;
    }

    // Genetablerer forbindelse
    public void reconnect() {
        closeConnection(); // Luk den gamle forbindelse, hvis den findes
        connect(); // Opret en ny forbindelse
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
