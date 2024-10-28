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

    // Metode til at hente singleton-instansen
    public static CupcakeService getInstance() {

        return instance;
    }

    // Hent bunde
    public List<String> getBottoms() throws SQLException {
        Connection connection = dbController.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT bottom FROM bottoms");

        List<String> bottoms = new ArrayList<>();
        while (rs.next()) {
            bottoms.add(rs.getString("bottom"));
        }

        return bottoms;
    }

    // Hent toppings
    public List<String> getToppings() throws SQLException {
        Connection connection = dbController.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT topping FROM toppings");

        List<String> toppings = new ArrayList<>();
        while (rs.next()) {
            toppings.add(rs.getString("topping"));
        }

        return toppings;
    }

    // Tilføj ordre til kurven og databasen
    public void addToCart(String customerName, String bottom, String topping, int quantity, boolean isPaid) throws SQLException {
        Connection connection = dbController.getConnection();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            connection.setAutoCommit(false);  //Start transaktion

            // Beregn priser
            double bottomPrice = getPriceFromDatabase("bottoms", bottom, connection);
            double toppingPrice = getPriceFromDatabase("toppings", topping, connection);
            double totalPrice = (bottomPrice + toppingPrice) * quantity;

            // Tilføj ordren til en lokal kurv
            cart.add(new OrderLine(bottom, topping, quantity, totalPrice));

            // order_details som JSON
            String orderDetailsJson = objectMapper.writeValueAsString(
                    Map.of(
                            "bottom", bottom,
                            "topping", topping,
                            "quantity", quantity
                    )
            );

            // Indsæt ordren i databasen
            String query = "INSERT INTO orders (customer_name, order_details, order_date, status, total_price) VALUES (?, ?::jsonb, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, customerName);
                statement.setObject(2, orderDetailsJson);
                statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                statement.setString(4, isPaid ? "betalt" : "ikke betalt");
                statement.setDouble(5, totalPrice);

                statement.executeUpdate();
                connection.commit();  // Bekræft transaktion
                System.out.println("Order was added to the database for customer: " + customerName);
            } catch (SQLException e) {
                connection.rollback();  // Undo hvis fejl skulle forekomme
                throw new SQLException("Error inserting order into the database: " + e.getMessage());
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Fjern en ordre fra kurven og databasen
    public void removeFromCart(int orderLineId) throws SQLException {
        Connection connection = dbController.getConnection();

        try {
            connection.setAutoCommit(false);  // Start transaktion

            // Fjern fra databasen
            String query = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, orderLineId);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Ordre med ID " + orderLineId + " blev slettet fra databasen.");
                    connection.commit();  // Bekræft transaktionen
                } else {
                    connection.rollback();  // Rul tilbage hvis ingen rækker blev slettet
                    System.out.println("Ordre med ID " + orderLineId + " blev ikke fundet.");
                }
            }
        } catch (SQLException e) {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();  // Rul transaktionen tilbage ved fejl, hvis forbindelsen er åben
            }
            throw new SQLException("Fejl ved fjernelse af ordre fra databasen: " + e.getMessage());
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }

        // Fjern fra den lokale kurv
        cart.removeIf(orderLine -> orderLine.getId() == orderLineId);
    }


    // Beregn totalprisen for kurven
    public double calculateTotalPrice() {
        return cart.stream().mapToDouble(OrderLine::getTotalPrice).sum();
    }

    // Hent prisen fra databasen for en given bund eller topping
    private double getPriceFromDatabase(String table, String name, Connection connection) throws SQLException {
        String column = table.equals("toppings") ? "topping" : "bottom";
        PreparedStatement stmt = connection.prepareStatement("SELECT price FROM " + table + " WHERE " + column + " = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        double price = 0;
        if (rs.next()) {
            price = rs.getDouble("price");
        }

        return price;
    }

    public List<OrderLine> getCartItems() {
        return new ArrayList<>(cart);
    }
}
