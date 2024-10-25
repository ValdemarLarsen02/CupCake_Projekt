package app.services;

import app.models.OrderLine;
import app.controllers.DatabaseController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CupcakeService {

    private final List<OrderLine> cart = new ArrayList<>();
    private final DatabaseController dbController = new DatabaseController();

    //Hent bunde
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

    //Hent toppings
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

    public void addToCart(String bottom, String topping, int quantity) throws SQLException {
        double bottomPrice = getPriceFromDatabase("bottoms", bottom);
        double toppingPrice = getPriceFromDatabase("toppings", topping);
        double totalPrice = (bottomPrice + toppingPrice) * quantity;

        cart.add(new OrderLine(bottom, topping, quantity, totalPrice));
    }

    public List<OrderLine> getCartItems() {
        return cart;
    }

    public void removeFromCart(int orderLineId) {
        cart.removeIf(orderLine -> orderLine.getId() == orderLineId);
    }

    public double calculateTotalPrice() {
        return cart.stream().mapToDouble(OrderLine::getTotalPrice).sum();
    }

    private double getPriceFromDatabase(String table, String name) throws SQLException {
        Connection connection = dbController.getConnection();
        // Bestem kolonnen baseret på tabellen
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
}
