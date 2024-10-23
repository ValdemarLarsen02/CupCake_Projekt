package app.admin;
import app.controllers.DatabaseController;
import app.entities.Order;
import app.entities.OrderDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin {





    public List<Order> getOrders() {
        DatabaseController db = new DatabaseController();
        Connection connection = db.getConnection();
        List<Order> orders = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM orders";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String customerName = resultSet.getString("customer_name");
                String status = resultSet.getString("status");
                String orderDetailsJson = resultSet.getString("order_details");

                // Parse JSON til OrderDetails objekt
                OrderDetails orderDetails = objectMapper.readValue(orderDetailsJson, OrderDetails.class);

                // Tilføj ordren til listen
                orders.add(new Order(orderId, customerName, status, orderDetails));
            }

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        } finally {
            db.closeConnection();  // Lukker forbindelse
        }

        return orders;  // Returner listen af ordrer
    }

    public void deleteOrder(int orderId) {

        DatabaseController db = new DatabaseController();
        Connection connection = db.getConnection();
        try {
            String query = "DELETE FROM orders WHERE order_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            statement.executeUpdate();

            System.out.println("Order med ID " + orderId + " blev slettet.");

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }
}
