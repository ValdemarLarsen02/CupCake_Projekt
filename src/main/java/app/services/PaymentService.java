package app.services;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentService {

    private final DatabaseController dbController = new DatabaseController();

    // Metode til at håndtere betalingen af en ordre
    public boolean processPayment(int customerId, double amount) {
        Connection connection = dbController.getConnection();
        try {
            // Opdater kundens saldo eller betalingsstatus i databasen
            String sql = "UPDATE customers SET balance = balance - ? WHERE customer_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, customerId);
                int rowsAffected = stmt.executeUpdate();

                // Hvis vi opdaterede en række, var betalingen succesfuld
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
