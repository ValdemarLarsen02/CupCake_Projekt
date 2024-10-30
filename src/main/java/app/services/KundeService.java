package app.services;

import app.entities.Customer;
import app.controllers.DatabaseController;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KundeService {
    private final DatabaseController databaseController;

    // Constructor som tager DatabaseController som parameter
    public KundeService(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    // Metode til at registrere en ny kunde
    public void createCustomer(Context ctx) {
        String name = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String email = ctx.formParam("email");
        String phoneStr = ctx.formParam("phone");
        int phone = Integer.parseInt(phoneStr);

        if (password1.equals(password2)) {
            try {
                createCustomerInDatabase(name, password1, email, phone);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + name + ". Nu skal du logge på.");
                ctx.render("Index.html");
            } catch (SQLException e) {
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind");
                ctx.render("Index.html");
            }
        } else {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("createcustomer.html");
        }
    }

    // Metode til at oprette en kunde i databasen
    private void createCustomerInDatabase(String name, String password, String email, int phone) throws SQLException {
        Connection connection = databaseController.getConnection();
        String sql = "INSERT INTO customers (name, email, phone, role, password, balance) VALUES (?, ?, ?, 'kunde', ?, 0.0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, phone);
            stmt.setString(4, password);
            stmt.executeUpdate();
        }
    }

    // Metode til at logge en kunde ind
    public void loginCustomer(Context ctx) {
        String name = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            Customer customer = getCustomerFromDatabase(name, password);
            if (customer != null) {
                ctx.sessionAttribute("currentUser", customer);
                ctx.render("Index.html"); // Her bliver man sendt til hjemmesiden når man logger ind
            } else {
                ctx.attribute("message", "Forkert brugernavn eller adgangskode. Prøv igen.");
                ctx.render("Index.html");
            }
        } catch (SQLException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("Index.html");
        }
    }

    // Metode til at hente en kunde fra databasen ved login
    private Customer getCustomerFromDatabase(String name, String password) throws SQLException {
        Connection connection = databaseController.getConnection();
        String sql = "SELECT * FROM customers WHERE name = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String email = rs.getString("email");
                int phone = rs.getInt("phone");
                String role = rs.getString("role");
                double balance = rs.getDouble("balance");
                return new Customer(name, email, phone, role, customerId, password, balance, connection);
            }
        }
        return null;
    }

    // Metode til at logge en kunde ud
    public void logoutCustomer(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.render("WelcomePage.html");
    }
}
