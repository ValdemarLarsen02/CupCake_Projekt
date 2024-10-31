package app.entities;

import app.controllers.DatabaseController;
import app.utils.ErrorLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerInformation {

    public List<Order> getCustomerOrders(int userId) {
        DatabaseController db = new DatabaseController();
        Connection connection = null;
        List<Order> orders = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

        try {
            connection = db.getConnection();
            String query = "SELECT o.order_id, o.customer_name, o.status, o.order_details, o.user_id, o.total_price, ca.balance " +
                    "FROM orders o " +
                    "LEFT JOIN customer_accounts ca ON o.user_id = ca.user_id " +
                    "WHERE o.user_id = ?"; // Tilføjet WHERE-klausul for at filtrere ordrer baseret på userId
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId); // Sætter userId som parameter
            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String customerName = resultSet.getString("customer_name");
                String status = resultSet.getString("status");
                String orderDetailsJson = resultSet.getString("order_details");
                double balance = resultSet.getDouble("balance"); // Hent saldo fra customer_accounts
                double orderAmount = resultSet.getDouble("total_price");
                // Parse JSON til OrderDetails objekt
                OrderDetails orderDetails = objectMapper.readValue(orderDetailsJson, OrderDetails.class);

                // Tilføj ordren til listen
                orders.add(new Order(orderId, customerName, status, orderDetails, userId, balance, orderAmount)); // Opdateret Order konstruktor
            }

        } catch (SQLException | IOException ex) {
            ErrorLogger.logError(ex);
        } finally {
            if (connection != null) {
                db.closeConnection();  // Lukker forbindelse
            }
        }

        return orders;  // Returner listen af ordrer
    }

    public static boolean removeOrder(int orderId) { // Lavet static for at vi kan kalde vores removeOrder.
        DatabaseController db = new DatabaseController();
        Connection connection = null;
        try {
            connection = db.getConnection();
            String deleteQuery = "DELETE FROM orders WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setInt(1, orderId);

            // Udfør DELETE forespørgslen
            int rowsAffected = statement.executeUpdate();

            // Returner korrekt hvis slettet.
            return rowsAffected > 0;
        } catch (SQLException e) {
            ErrorLogger.logError(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Luk forbindelsen
                } catch (SQLException e) {
                    ErrorLogger.logError(e);
                }
            }
        }
        return false;
    }
}
