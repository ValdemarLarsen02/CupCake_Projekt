package app.entities;



public class OrderDetails {
    private String bottoms;
    private int quantity;
    private String toppings;

    // Getters og Setters
    public String getBottoms() {
        return bottoms;
    }

    public void setBottoms(String bottoms) {
        this.bottoms = bottoms;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getToppings() {
        return toppings;
    }

    public void setToppings(String toppings) {
        this.toppings = toppings;
    }
}