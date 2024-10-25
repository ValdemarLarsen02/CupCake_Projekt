package app.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    private String name;
    private String email;
    private int phone;
    private String role;
    private int userid;
    private String password;
    private Connection connection;

    // Constructor modtager kun Connection, da vi henter andre data dynamisk fra databasen
    public Admin(Connection connection) {
        this.connection = connection;
    }

    // Metode til at hente admin-data fra databasen baseret på userid
    public boolean loadAdminById(int userid) {
        String sql = "SELECT name, email, phone, role, password FROM admins WHERE userid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.name = rs.getString("name");
                this.email = rs.getString("email");
                this.phone = rs.getInt("phone");
                this.role = rs.getString("role");
                this.password = rs.getString("password");
                this.userid = userid;
                return true;  // Admin fundet og data indlæst
            } else {
                System.out.println("Ingen admin fundet med ID: " + userid);
                return false;  // Ingen admin fundet
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Fejl under databaseoperation
        }
    }

    // Metode til at indsætte penge på kundens konto
    public void depositFunds(int customerId, double amount) {
        if (connection == null) {
            System.err.println("Forbindelse til databasen er ikke tilgængelig.");
            return;
        }

        String sql = "UPDATE customers SET balance = balance + ? WHERE customer_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Sæt beløb og kunde-ID i forespørgslen
            statement.setDouble(1, amount);
            statement.setInt(2, customerId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(amount + " blev indsat på kundens konto med ID: " + customerId);
            } else {
                System.out.println("Ingen kunde fundet med ID: " + customerId);
            }

        } catch (SQLException e) {
            System.err.println("Fejl ved indsættelse af penge på kundens konto: " + e.getMessage());
        }
    }

    // Getters for Admin attributter
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public int getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }
}
