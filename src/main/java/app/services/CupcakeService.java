package app.services;

import app.models.OrderLine;
import app.controllers.DatabaseController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CupcakeService {

    private static final CupcakeService instance = new CupcakeService();
    private final List<OrderLine> cart = new ArrayList<>();
    private final DatabaseController dbController = new DatabaseController();

    private CupcakeService() {
    }

    // Singleton pattern for at sikre én instans af CupcakeService
    public static CupcakeService getInstance() {
        return instance;
    }

    // Hent bunde fra databasen og returner som en liste af strings
    public List<String> getBottoms() {
        List<String> bottoms = new ArrayList<>();
        try (Connection connection = dbController.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bottom FROM bottoms")) {

            while (rs.next()) {
                bottoms.add(rs.getString("bottom"));
            }
        } catch (SQLException e) {
            System.out.println("Fejl ved hentning af bunde: " + e.getMessage());
        }
        return bottoms;
    }

    // Hent toppings fra databasen og returner som en liste af strings
    public List<String> getToppings() {
        List<String> toppings = new ArrayList<>();
        try (Connection connection = dbController.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT topping FROM toppings")) {

            while (rs.next()) {
                toppings.add(rs.getString("topping"));
            }
        } catch (SQLException e) {
            System.out.println("Fejl ved hentning af toppings: " + e.getMessage());
        }
        return toppings;
    }

    // Tilføj ordre til kurv og databasen
    public void addToCart(int userId, String customerName, String bottom, String topping, int quantity, boolean isPaid) {
        try (Connection connection = dbController.getConnection()) {
            connection.setAutoCommit(false); // Start en database-transaktion

            // Beregn priser for bund og topping
            double bottomPrice = getPriceFromDatabase("bottoms", bottom, connection);
            double toppingPrice = getPriceFromDatabase("toppings", topping, connection);
            double totalPrice = (bottomPrice + toppingPrice) * quantity;

            // Tilføj ordren til den lokale kurv
            cart.add(new OrderLine(bottom, topping, quantity, totalPrice));

            // Konverter ordreoplysninger til JSON-format
            ObjectMapper objectMapper = new ObjectMapper();
            String orderDetailsJson = objectMapper.writeValueAsString(Map.of(
                    "bottom", bottom,
                    "topping", topping,
                    "quantity", quantity
            ));

            // Opret en ny ordre i databasen
            String query = "INSERT INTO orders (user_id, customer_name, order_details, order_date, status, total_price) VALUES (?, ?, ?::jsonb, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                statement.setString(2, customerName);
                statement.setObject(3, orderDetailsJson);
                statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                statement.setString(5, isPaid ? "betalt" : "ikke betalt");
                statement.setDouble(6, totalPrice);

                statement.executeUpdate();
                connection.commit();  // Bekræft transaktionen
            } catch (SQLException e) {
                connection.rollback();  // Rul tilbage ved fejl i databaseoperationen
                System.out.println("Fejl ved indsættelse af ordre i databasen: " + e.getMessage());
            }
        } catch (SQLException | JsonProcessingException e) {
            System.out.println("Fejl i addToCart: " + e.getMessage());
        }
    }

    // Fjern en ordre fra kurven og databasen
    public void removeFromCart(int orderLineId) {
        try (Connection connection = dbController.getConnection()) {
            connection.setAutoCommit(false);  // Start en database-transaktion

            // Fjern ordren fra databasen
            String query = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, orderLineId);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Ordre med ID " + orderLineId + " blev slettet fra databasen.");
                    connection.commit();  // Bekræft transaktionen
                } else {
                    connection.rollback();  // Rul tilbage, hvis ingen rækker blev slettet
                    System.out.println("Ordre med ID " + orderLineId + " blev ikke fundet.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Fejl ved fjernelse af ordre fra databasen: " + e.getMessage());
        }

        // Fjern ordren fra den lokale kurv
        cart.removeIf(orderLine -> orderLine.getId() == orderLineId);
    }

    // Beregn totalprisen for alle varer i kurven
    public double calculateTotalPrice() {
        double total = 0.0;
        for (OrderLine orderLine : cart) {
            total += orderLine.getTotalPrice();
        }
        return total;
    }

    // Hent prisen for en given bund eller topping fra databasen
    private double getPriceFromDatabase(String table, String name, Connection connection) {
        double price = 0;
        String column = table.equals("toppings") ? "topping" : "bottom";
        String query = "SELECT price FROM " + table + " WHERE " + column + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    price = rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            System.out.println("Fejl ved hentning af pris fra " + table + ": " + e.getMessage());
        }
        return price;
    }


    public List<OrderLine> getCartItems() {
        return new ArrayList<>(cart);
    }
}