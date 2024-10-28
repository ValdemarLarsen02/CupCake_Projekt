package app.entities;

import app.controllers.DatabaseController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private static DatabaseController dbController;
    private static Connection connection;
    private static Admin admin;

    @BeforeAll
    static void setup() {
        dbController = new DatabaseController();
        connection = dbController.getConnection();
        admin = new Admin(connection);
    }

    @Test
    void testLoadAdminById_validId() {
        // Forudsætter, at der findes en admin i databasen med ID 1
        boolean found = admin.loadAdminById(1);
        assertTrue(found, "Admin med ID 1 blev ikke fundet.");
    }

    @Test
    void testLoadAdminById_invalidId() {
        // Prøv med et ID, som vi ved ikke findes
        boolean found = admin.loadAdminById(999);
        assertFalse(found, "Admin med ID 999 burde ikke findes.");
    }

    @Test
    void testDepositFunds_validCustomer() {
        // Forudsætter, at der findes en kunde med ID 1
        int customerId = 1;
        double initialBalance = getCustomerBalance(customerId);
        double amount = 50.00;

        admin.depositFunds(customerId, amount);

        double newBalance = getCustomerBalance(customerId);
        assertEquals(initialBalance + amount, newBalance, "Saldoen burde være opdateret for kunde med ID " + customerId);
    }

    // Hjælpemetode til at hente kundens nuværende saldo fra databasen
    private double getCustomerBalance(int customerId) {
        String sql = "SELECT balance FROM customers WHERE customer_id = " + customerId;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Returner -1 som indikator, hvis der opstår en fejl
    }

    @AfterAll
    static void tearDown() {
        dbController.closeConnection();
    }
}
