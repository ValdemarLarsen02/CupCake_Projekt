package app.controllers;

import app.entities.Customer;
import app.services.CupcakeService;
import app.services.PaymentService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class PaymentController {

    private static final PaymentService paymentService = new PaymentService();
    private static final CupcakeService cupcakeService = CupcakeService.getInstance();

    public static void registerRoutes(Javalin app) {
        // Rute til at vise betalingssiden
        app.get("/payment", ctx -> {
            var cartItems = cupcakeService.getCartItems();
            double totalPrice = cupcakeService.calculateTotalPrice();

            // Hent kunde fra sessionen
            Customer currentCustomer = ctx.sessionAttribute("currentUser");
            if (currentCustomer == null) {
                ctx.redirect("/login");
                return;
            }

            ctx.attribute("cartItems", cartItems);
            ctx.attribute("totalPrice", totalPrice);
            ctx.render("payment.html");
        });

        // Rute til at håndtere betalingsprocessen
        app.post("/process-payment", ctx -> processPayment(ctx));
    }

    private static void processPayment(Context ctx) {
        // Hent kunde fra sessionen
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

        // Brug kunde-id fra den loggede bruger i sessionen
        int customerId = currentCustomer.getCustomerId();
        boolean paymentSuccess = paymentService.processPayment(customerId, amount);

        if (paymentSuccess) {
            ctx.redirect("/confirmation");
        } else {
            ctx.status(500).result("Payment failed. Please try again.");
        }
    }
}
