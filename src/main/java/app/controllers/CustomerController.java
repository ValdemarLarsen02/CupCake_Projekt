package app.controllers;

import app.entities.CustomerInformation;
import app.entities.Order;
import app.entities.User;
import app.services.KundeService;
import app.controllers.DatabaseController;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CustomerController {

    private static final DatabaseController databaseController = new DatabaseController();
    private static final KundeService kundeService = new KundeService(databaseController);

    public static void registerRoutes(Javalin app) {
        // Route til at vise oprettelsesformularen

        app.get("/my-orders", ctx -> {
            // Hent currentUser fra sessionen
            User currentUser = ctx.sessionAttribute("currentUser");

            if (currentUser == null) {
                ctx.status(403).result("Adgang nægtet: Du skal være logget ind for at se dine ordrer.");
                return;
            }

            Integer userId = currentUser.getUserId(); // Hent userId fra currentUser
            System.out.println(userId);
            CustomerInformation customerInfo = new CustomerInformation();
            List<Order> userOrders = customerInfo.getCustomerOrders(userId);

            // Giv ordrer til Thymeleaf
            ctx.attribute("userOrders", userOrders);
            ctx.render("my-orders.html");  // Render my-orders.html skabelonen
        });


        app.post("/remove-order", ctx -> {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            System.out.println(orderId);

            boolean status = CustomerInformation.removeOrder(orderId);

            if (status) {
                ctx.render("my-orders.html");
            } else {
                System.out.println("Der gik noget galt");
            }

        });


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
