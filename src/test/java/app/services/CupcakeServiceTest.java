package app.services;

import app.controllers.DatabaseController;
import app.models.OrderLine;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CupcakeServiceTest {

    private static DatabaseController dbController;
    private static Connection connection;
    private CupcakeService cupcakeService;

    @BeforeAll
    static void setupDatabase() {
        dbController = new DatabaseController();
        connection = dbController.getConnection(); // Opret forbindelse
    }

    @AfterAll
    static void closeDatabase() {
        dbController.closeConnection();
    }

    @BeforeEach
    void setup() {
        cupcakeService = CupcakeService.getInstance(); // Brug singleton-instansen
    }

    @Test
    void testGetBottoms() throws SQLException {
        List<String> bottoms = cupcakeService.getBottoms();
        assertNotNull(bottoms, "Bottoms-listen må ikke være null.");
        assertFalse(bottoms.isEmpty(), "Bottoms-listen må ikke være tom.");
    }

    @Test
    void testGetToppings() throws SQLException {
        List<String> toppings = cupcakeService.getToppings();
        assertNotNull(toppings, "Toppings-listen må ikke være null.");
        assertFalse(toppings.isEmpty(), "Toppings-listen må ikke være tom.");
    }

    @Test
    void testAddToCart() throws SQLException {
        int userId = 1; // Brug et gyldigt userId
        String customerName = "Test Kunde";
        String bottom = "Vanilje"; // Sørg for at "Vanilje" findes i databasen
        String topping = "Chokolade"; // Sørg for at "Chokolade" findes i databasen
        int quantity = 2;
        boolean isPaid = false;

        cupcakeService.addToCart(userId, customerName, bottom, topping, quantity, isPaid);

        List<OrderLine> cartItems = cupcakeService.getCartItems();
        assertFalse(cartItems.isEmpty(), "Kurven bør have mindst én vare efter tilføjelse.");
        OrderLine lastOrder = cartItems.get(cartItems.size() - 1);
        assertEquals(bottom, lastOrder.getBottom());
        assertEquals(topping, lastOrder.getTopping());
        assertEquals(quantity, lastOrder.getQuantity());
    }

    @Test
    void testRemoveFromCart() throws SQLException {
        int orderLineId = 1; // Sæt her et eksisterende orderLineId

        cupcakeService.removeFromCart(orderLineId);

        List<OrderLine> cartItems = cupcakeService.getCartItems();
        assertTrue(cartItems.stream().noneMatch(order -> order.getId() == orderLineId),
                "Ordrelinjen bør være fjernet fra kurven.");
    }

    @Test
    void testCalculateTotalPrice() {
        double totalPrice = cupcakeService.calculateTotalPrice();
        assertTrue(totalPrice >= 0, "Den samlede pris bør være lig med eller større end nul.");
    }
}
