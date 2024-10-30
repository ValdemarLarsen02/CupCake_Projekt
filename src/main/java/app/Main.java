package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.*;
import app.services.CupcakeService;
import app.services.PaymentService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        // Initialiser Javalin og Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Initialiser DatabaseController
        DatabaseController dbController = new DatabaseController();
        // Initialiser services
        CupcakeService cupcakeService = CupcakeService.getInstance();
        PaymentService paymentService = new PaymentService(dbController.getConnection());

        // Initialiser controllers
        CupcakeController.registerRoutes(app);
        AdminController.registerRoutes(app);
        CustomerController.registerRoutes(app);
        UserController.addRoutes(app, dbController);
        PaymentController paymentController = new PaymentController(paymentService, cupcakeService);
        paymentController.registerRoutes(app);
    }
}
