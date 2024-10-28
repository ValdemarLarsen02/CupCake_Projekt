package app.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentService {

    private final Connection connection;

    public PaymentService(Connection connection) {
        this.connection = connection;
    }

    public boolean processPayment(int customerId, double amount) {
        String checkBalanceSQL = "SELECT balance FROM customers WHERE customer_id = ?";
        String updateBalanceSQL = "UPDATE customers SET balance = balance - ? WHERE customer_id = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkBalanceSQL)) {
            checkStmt.setInt(1, customerId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");

                // Tjekker om kunden har nok penge til at gennemføre betalingen
                if (balance < amount) {
                    return false;  // Ikke tilstrækkelig balance
                }

                try (PreparedStatement updateStmt = connection.prepareStatement(updateBalanceSQL)) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, customerId);
                    int rowsUpdated = updateStmt.executeUpdate();
                    return rowsUpdated > 0;  // Returnerer true, hvis betalingen gik igennem
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
