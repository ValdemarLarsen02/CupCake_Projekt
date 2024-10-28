package app.controllers;

import app.services.KundeService;
import app.controllers.DatabaseController;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class CustomerController {

    private static final DatabaseController databaseController = new DatabaseController();
    private static final KundeService kundeService = new KundeService(databaseController);

    public static void registerRoutes(Javalin app) {
        // Route til at vise oprettelsesformularen
        app.get("/create-user", ctx -> showCreateUserForm(ctx));

        // Route til at oprette en ny kunde
        app.post("/create-user", ctx -> createUser(ctx));

        // Route til at vise loginformularen
        app.get("/login", ctx -> showLoginForm(ctx));

        // Route til at logge en kunde ind
        app.post("/login", ctx -> loginUser(ctx));

        // Route til at logge en kunde ud
        app.post("/logout", ctx -> logoutUser(ctx));
    }

    // Metode til at vise oprettelsesformularen
    private static void showCreateUserForm(Context ctx) {
        ctx.render("createuser.html");
    }

    // Metode til at oprette en ny kunde
    private static void createUser(Context ctx) {
        kundeService.createCustomer(ctx);
    }

    // Metode til at vise loginformularen
    private static void showLoginForm(Context ctx) {
        ctx.render("login.html");
    }

    // Metode til at logge en kunde ind
    private static void loginUser(Context ctx) {
        kundeService.loginCustomer(ctx);
    }

    // Metode til at logge en kunde ud
    private static void logoutUser(Context ctx) {
        kundeService.logoutCustomer(ctx);
    }
}
