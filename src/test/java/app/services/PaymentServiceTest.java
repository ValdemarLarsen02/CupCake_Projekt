package app.services;

import app.controllers.DatabaseController;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private static DatabaseController dbController;
    private static Connection connection;
    private PaymentService paymentService;

    @BeforeAll
    static void setupDatabase() {
        dbController = new DatabaseController();
        connection = dbController.getConnection(); // Hent forbindelse fra dbController
    }

    @AfterAll
    static void closeDatabase() {
        dbController.closeConnection();
    }

    @BeforeEach
    void setup() {
        paymentService = new PaymentService(connection);
    }

    @Test
    void testProcessPayment_sufficientBalance() {
        int customerId = 1; // Husk at sætte en gyldig kunde-id i databasen med tilstrækkelig balance
        double amount = 50.0;

        boolean result = paymentService.processPayment(customerId, amount);
        assertTrue(result, "Betalingen burde være gennemført med tilstrækkelig balance.");
    }

    @Test
    void testProcessPayment_insufficientBalance() {
        int customerId = 2; // Husk at sætte en gyldig kunde-id i databasen med utilstrækkelig balance
        double amount = 200.0;

        boolean result = paymentService.processPayment(customerId, amount);
        assertFalse(result, "Betalingen burde fejle ved utilstrækkelig balance.");
    }
}
