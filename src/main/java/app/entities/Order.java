package app.entities;

public class Order {
    private int orderId;
    private String customerName;
    private String status;
    private OrderDetails orderDetails;  // Ny tilføjelse

    public Order(int orderId, String customerName, String status, OrderDetails orderDetails) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.orderDetails = orderDetails;
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
}
