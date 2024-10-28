package app.controllers;

import app.entities.Admin;
import io.javalin.Javalin;

import java.sql.Connection;

public class AdminController {

    private final Connection connection;

    // Constructor der modtager en databaseforbindelse
    public AdminController(Connection connection) {
        this.connection = connection;
    }

    // Registrer ruter
    public void registerRoutes(Javalin app) {
        app.get("/admin", ctx -> {
            int userid = 1;  // Dette userid kan komme fra login-session eller en anden kilde
            Admin admin = new Admin(connection);
            boolean found = admin.loadAdminById(userid);

            if (found) {
                // Sæt attributter baseret på de indlæste admin-data
                ctx.attribute("adminName", admin.getName());
                ctx.attribute("adminEmail", admin.getEmail());
                ctx.render("admin.html");  // Render admin.html skabelonen med data
            } else {
                ctx.status(404).result("Admin ikke fundet.");
            }
        });

        // Eksempel på at bruge admin i en anden route, f.eks. indsættelse af penge
        app.post("/admin/indsaet-beloeb", ctx -> {
            String customerIdParam = ctx.formParam("customerId");
            String amountParam = ctx.formParam("amount");

            if (customerIdParam == null || amountParam == null) {
                ctx.status(400).result("Kunde ID eller beløb mangler.");
                return;
            }

            int customerId = Integer.parseInt(customerIdParam);
            double amount = Double.parseDouble(amountParam);

            Admin admin = new Admin(connection);
            admin.loadAdminById(1);  // Antager en admin session

            admin.depositFunds(customerId, amount);  // Brug admin til at indsætte penge på kundekonto

            ctx.redirect("/admin");  // Tilbage til admin dashboard efter operation
        });
    }
}
