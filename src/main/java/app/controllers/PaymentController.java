package app.controllers;

import app.entities.Customer;
import app.services.CupcakeService;
import app.services.PaymentService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PaymentController {

    private final PaymentService paymentService;
    private final CupcakeService cupcakeService;

    // Constructor som modtager PaymentService og CupcakeService
    public PaymentController(PaymentService paymentService, CupcakeService cupcakeService) {
        this.paymentService = paymentService;
        this.cupcakeService = cupcakeService;
    }

    // Registrer routes som instansmetode
    public void registerRoutes(Javalin app) {
        app.get("/payment", this::showPaymentPage);
        app.post("/process-payment", this::processPayment);
    }

    // Metode til at vise betalingsside
    private void showPaymentPage(Context ctx) {
        Customer currentCustomer = ctx.sessionAttribute("currentUser");
        if (currentCustomer == null) {
            ctx.redirect("/login");
            return;
        }

        var cartItems = cupcakeService.getCartItems();
        double totalPrice = cupcakeService.calculateTotalPrice();

        ctx.attribute("cartItems", cartItems);
        ctx.attribute("totalPrice", totalPrice);
        ctx.attribute("customerName", currentCustomer.getName());
        ctx.render("payment.html");
    }

    // Metode til at håndtere betaling
    private void processPayment(Context ctx) {
        Customer currentCustomer = ctx.sessionAttribute("currentUser");
        if (currentCustomer == null) {
            ctx.redirect("/login");
            return;
        }

        String amountParam = ctx.formParam("amount");
        if (amountParam == null) {
            ctx.status(400).result("Amount is missing.");
            return;
        }

        double amount = Double.parseDouble(amountParam);
        int customerId = currentCustomer.getCustomerId();
        boolean paymentSuccess = paymentService.processPayment(customerId, amount);

        if (paymentSuccess) {
            ctx.redirect("/confirmation");
        } else {
            ctx.status(500).result("Payment failed. Please try again.");
        }
    }
}
