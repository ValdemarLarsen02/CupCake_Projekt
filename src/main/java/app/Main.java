package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.CupcakeController;
import app.controllers.CustomerController;
import app.controllers.DatabaseController;
import app.controllers.PaymentController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Starter databasen forbindelse
        DatabaseController db = new DatabaseController();

        // Registering routes
        CupcakeController.registerRoutes(app);
        PaymentController.registerRoutes(app);
        CustomerController.registerRoutes(app);
    }
}
