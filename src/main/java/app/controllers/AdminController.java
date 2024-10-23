package app.controllers;

import app.admin.Admin;
import io.javalin.Javalin;

public class AdminController {

    // Metode til at registrere ruter
    public static void registerRoutes(Javalin app) {
        app.get("/admin", ctx -> {
            Admin admin = new Admin();
            ctx.attribute("orders", admin.getOrders());  // Tilføj ordrer til Thymeleaf-modellen
            ctx.render("admin.html");  // Render admin.html skabelonen
        });
    }
}
