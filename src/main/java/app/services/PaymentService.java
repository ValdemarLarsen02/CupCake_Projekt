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
        String checkAccountSQL = "SELECT balance FROM customer_accounts WHERE user_id = ?";
        String createAccountSQL = "INSERT INTO customer_accounts (user_id, balance) VALUES (?, ?)";
        String updateBalanceSQL = "UPDATE customer_accounts SET balance = balance - ? WHERE user_id = ?";
        String updateOrderStatusSQL = "UPDATE orders SET status = 'betalt' WHERE user_id = ?"; // SQL til opdatering af ordre-status

        try (PreparedStatement checkStmt = connection.prepareStatement(checkAccountSQL)) {
            checkStmt.setInt(1, customerId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");

                // Tjekker om kunden har nok penge til at gennemføre betalingen
                if (balance < amount) {
                    return false;  // Ikke tilstrækkelig balance
                }

                // Opdater saldoen
                try (PreparedStatement updateStmt = connection.prepareStatement(updateBalanceSQL)) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, customerId);
                    int rowsUpdated = updateStmt.executeUpdate();

                    // Opdater ordre-status
                    try (PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderStatusSQL)) {
                        updateOrderStmt.setInt(1, customerId);
                        updateOrderStmt.executeUpdate(); // Opdater ordre-status til 'betalt'
                    }

                    return rowsUpdated > 0;  // Returnerer true, hvis betalingen gik igennem
                }
            } else {
                // Hvis brugeren ikke findes, opret en ny konto med en saldo på 100kr
                try (PreparedStatement createStmt = connection.prepareStatement(createAccountSQL)) {
                    createStmt.setInt(1, customerId);
                    createStmt.setDouble(2, 100);  // Start Saldo 100kr
                    int rowsInserted = createStmt.executeUpdate();

                    // Hvis kontoen blev oprettet, træk beløbet fra
                    if (rowsInserted > 0) {
                        // Kører nu metoden igen, efter konti er blevet oprettet!
                        return processPayment(customerId, amount);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
