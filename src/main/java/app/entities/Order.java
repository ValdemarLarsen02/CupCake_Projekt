package app.entities;

public class Order {
    private int orderId;
    private String customerName;
    private String status;
    private OrderDetails orderDetails;  // Ny tilføjelse

    private int user_id;

    private double balance;

    private double orderAmount;

    public Order(int orderId, String customerName, String status, OrderDetails orderDetails, int user_id, double balance, double orderAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.orderDetails = orderDetails;
        this.user_id = user_id;
        this.balance = balance;
        this.orderAmount = orderAmount;
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStatus() {
        return status;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public int getUser_id() {
        return user_id;
    }

    public double getBalance() {
        return balance;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

}