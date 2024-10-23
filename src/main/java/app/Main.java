package app;

import app.admin.Admin;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.DatabaseController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;


public class Main {
    public static void main(String[] args)
    {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing


        //Starter databasen forbindelse

        DatabaseController db = new DatabaseController();

        //Registering af routes:
        // Registrerer Admin-ruter
        AdminController.registerRoutes(app);


        app.get("/", ctx ->  ctx.render("index.html"));
    }
}