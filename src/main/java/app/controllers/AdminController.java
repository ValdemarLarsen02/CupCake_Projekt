package app.controllers;

import app.admin.Admin;
import io.javalin.Javalin;

public class AdminController {

    // Registering af vores routes / DATA TRANSFER.
    public static void registerRoutes(Javalin app) {
        app.get("/admin", ctx -> {
            Admin admin = new Admin();
            ctx.attribute("orders", admin.getOrders());
            ctx.attribute("kunder", admin.getUsers());
            ctx.render("admin.html");
        });


        app.post("/admin/delete-order", ctx -> {
            String orderIdParam = ctx.formParam("orderId");  // Henter orderId fra formen

            // Tjekker om vi korrekt modtager et orderId i vores post kald.
            if (orderIdParam == null) {
                ctx.status(400).result("Order ID mangler");
                return;
            }

            int orderId = Integer.parseInt(orderIdParam);  // Konverterer orderId til et heltal
            Admin admin = new Admin();
            admin.deleteOrder(orderId);
            ctx.redirect("/admin");  // Efter sletning, redirect til admin-siden
        });

        // Route til opdatering/indsættelse af kundes konto.
        app.post("/admin/upsert-customer-account", ctx -> {
            String userIdParam = ctx.formParam("userId"); // brugersn id.
            String balanceParam = ctx.formParam("initialBalance"); // Saldo der bliver indsat
            // Tjekker om vi korrekt modtager userId og balance i vores post kald.
            if (userIdParam == null || balanceParam == null) {
                ctx.status(400).result("User ID eller balance mangler");
                return;
            }

            try {
                int userId = Integer.parseInt(userIdParam);
                double initialBalance = Double.parseDouble(balanceParam);

                Admin admin = new Admin();
                boolean success = admin.upsertCustomerAccount(userId, initialBalance);

                if (success) {
                    ctx.redirect("/admin"); // Redirect til admin-siden efter succes
                } else {
                    ctx.status(500).result("Fejl under opdatering/indsættelse af konto");
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Ugyldig format for User ID eller balance");
            }

        });

        app.post("/admin/create-payment", ctx -> {
            String orderIdParam = ctx.formParam("orderId");
            String amountParam = ctx.formParam("amount");

            // Tjekker om vi korrekt modtager orderId og amount i vores post kald.
            if (orderIdParam == null || amountParam == null) {
                ctx.status(400).result("Order ID eller beløb mangler");
                return;
            }

            try {
                int orderId = Integer.parseInt(orderIdParam);  // Konverterer orderId til et heltal
                double amount = Double.parseDouble(amountParam);  // Konverterer beløb til double

                Admin admin = new Admin();
                boolean success = admin.createPayment(orderId, amount);

                if (success) {
                    ctx.redirect("/admin"); // Redirect til admin-siden efter succes
                } else {
                    ctx.status(500).result("Fejl under opkrævning af betaling");
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Ugyldig format for Order ID eller beløb");
            }
        });
    }
}
