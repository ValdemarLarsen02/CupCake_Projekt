package app.models;

public class OrderLine {
    private static int idCounter = 0;
    private final int id;
    private final String bottom;
    private final String topping;
    private final int quantity;
    private final double totalPrice;

    public OrderLine(String bottom, String topping, int quantity, double totalPrice) {
        this.id = ++idCounter;
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public String getBottom() {
        return bottom;
    }

    public String getTopping() {
        return topping;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
