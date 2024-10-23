package app.controllers;

import app.admin.Admin;
import io.javalin.Javalin;

public class AdminController {

    // Registering af vores routes / DATA TRANSFER.
    public static void registerRoutes(Javalin app) {
        app.get("/admin", ctx -> {
            Admin admin = new Admin();
            ctx.attribute("orders", admin.getOrders());
            ctx.render("admin.html");  // Render admin.html skabelonen
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
    }
}

