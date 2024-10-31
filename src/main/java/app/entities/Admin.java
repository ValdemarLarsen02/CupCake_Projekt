package app.admin;
import app.controllers.DatabaseController;
import app.entities.CustomerDetails;
import app.entities.Order;
import app.entities.OrderDetails;
import app.entities.User;
import app.utils.ErrorLogger;
import com.fasterxml.jackson.databind.ObjectMapper; // Håndtering af JSON DATA

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin {


    public List<Order> getOrders() {
        DatabaseController db = new DatabaseController();
        Connection connection = null;
        List<Order> orders = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

        try {
            connection = db.getConnection();
            Statement statement = connection.createStatement();

            // Opdateret SQL-forespørgsel til at inkludere saldo fra customer_accounts
            String query = "SELECT o.order_id, o.customer_name, o.status, o.order_details, o.user_id, o.total_price, ca.balance " +
                    "FROM orders o " +
                    "LEFT JOIN customer_accounts ca ON o.user_id = ca.user_id"; // Left join for at inkludere alle ordrer

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String customerName = resultSet.getString("customer_name");
                String status = resultSet.getString("status");
                String orderDetailsJson = resultSet.getString("order_details");
                int userId = resultSet.getInt("user_id");
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
            ErrorLogger.logError(ex);
        } finally {
            db.closeConnection();
        }
    }

    public boolean upsertCustomerAccount(int userId, double initialBalance) {
        DatabaseController db = new DatabaseController();
        PreparedStatement checkAccountStmt = null;
        PreparedStatement updateAccountStmt = null;
        PreparedStatement insertAccountStmt = null;
        Connection connection = null;

        try {
            connection = db.getConnection();

            // Tjek om brugeren har en konto
            String checkAccountQuery = "SELECT user_id FROM customer_accounts WHERE user_id = ?";
            checkAccountStmt = connection.prepareStatement(checkAccountQuery);
            checkAccountStmt.setInt(1, userId);
            ResultSet resultSet = checkAccountStmt.executeQuery();

            if (resultSet.next()) {
                // Konto findes, opdater saldoen
                String updateAccountQuery = "UPDATE customer_accounts SET balance = ? WHERE user_id = ?";
                updateAccountStmt = connection.prepareStatement(updateAccountQuery);
                updateAccountStmt.setDouble(1, initialBalance);
                updateAccountStmt.setInt(2, userId);
                updateAccountStmt.executeUpdate();

                System.out.println("Saldo opdateret for bruger med ID " + userId);
            } else {
                // Konto findes ikke, indsæt ny konto
                String insertAccountQuery = "INSERT INTO customer_accounts (user_id, balance) VALUES (?, ?)";
                insertAccountStmt = connection.prepareStatement(insertAccountQuery);
                insertAccountStmt.setInt(1, userId);
                insertAccountStmt.setDouble(2, initialBalance);
                insertAccountStmt.executeUpdate();

                System.out.println("Konto oprettet for bruger med ID " + userId);
            }

            return true;

        } catch (SQLException e) {
            // Håndter SQL-fejl
            e.printStackTrace();
            return false;
        } finally {
            // Luk forbindelsen og statements
            try {
                if (checkAccountStmt != null) checkAccountStmt.close();
                if (updateAccountStmt != null) updateAccountStmt.close();
                if (insertAccountStmt != null) insertAccountStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean createPayment(int orderId, double paymentAmount) {
        DatabaseController db = new DatabaseController();
        Connection connection = null;
        PreparedStatement updateBalanceStmt = null;
        PreparedStatement updateOrderStatusStmt = null;

        try {
            connection = db.getConnection();
            //Finder user_id for ordren.
            String getUserIdQuery = "SELECT user_id FROM orders WHERE order_id = ?";
            PreparedStatement getUserIdStmt = connection.prepareStatement(getUserIdQuery);
            getUserIdStmt.setInt(1, orderId);
            ResultSet userIdResult = getUserIdStmt.executeQuery();



            //Hvis fundes:
            if (userIdResult.next()) {
                //Opdater saldo for brugeren samt status i orders.
                int userId = userIdResult.getInt("user_id");


                String updateBalanceQuery = "UPDATE customer_accounts SET balance = balance - ? WHERE user_id = ?";
                updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
                updateBalanceStmt.setDouble(1, paymentAmount);
                updateBalanceStmt.setInt(2, userId);
                updateBalanceStmt.executeUpdate();


                //Opdatere status i orders:
                String updateOrderStatusQuery = "UPDATE orders SET status = 'betalt' WHERE order_id = ?";
                updateOrderStatusStmt = connection.prepareStatement(updateOrderStatusQuery);
                updateOrderStatusStmt.setInt(1, orderId);
                updateOrderStatusStmt.executeUpdate();

                System.out.println("Betaling registreret for ordre ID " + orderId + " med beløb: " + paymentAmount);
                return true;
            } else {
                System.out.println("Ordre ID " + orderId + " findes ikke.");
                return false;

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Luk forbindelsen og statements
            try {
                if (updateBalanceStmt != null) updateBalanceStmt.close();
                if (updateOrderStatusStmt != null) updateOrderStatusStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<CustomerDetails> getUsers() {
        DatabaseController db = new DatabaseController();
        Connection connection = null;
        List<CustomerDetails> customers = new ArrayList<>();

        try {
            connection = db.getConnection();
            Statement statement = connection.createStatement();

            // Hent brugere med rolle 'kunde'
            String customersQuery = "SELECT * FROM users WHERE role = 'kunde'";
            ResultSet resultSet = statement.executeQuery(customersQuery);

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");

                // Tjek om brugeren findes i customer_accounts
                String accountCheckQuery = "SELECT balance FROM customer_accounts WHERE user_id = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(accountCheckQuery)) {
                    checkStmt.setInt(1, userId);
                    ResultSet accountResult = checkStmt.executeQuery();

                    // Hvis brugeren findes i customer_accounts
                    if (accountResult.next()) {
                        double balance = accountResult.getDouble("balance");
                        // Opret en CustomerDetails-instans og tilføj til listen
                        CustomerDetails customerDetails = new CustomerDetails(userId, username, role, balance);
                        customers.add(customerDetails);
                        System.out.println(customers);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Luk forbindelsen
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(customers);
        return customers; // Returner listen af kunder
    }


}