package app.controllers;

import app.entities.User;
import app.persistence.UserMapper;
import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.controllers.DatabaseController;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;

public class UserController {

    public static void addRoutes(Javalin app, DatabaseController databaseController) {
        app.post("login", ctx -> login(ctx, databaseController));
        app.get("logout", UserController::logout);
        app.get("createuser", ctx -> ctx.render("createcustomer.html"));
        app.post("createuser", ctx -> createUser(ctx, databaseController));
    }

    private static void createUser(Context ctx, DatabaseController databaseController) {
        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1 != null && password1.equals(password2)) {
            String hashedPassword = BCrypt.hashpw(password1, BCrypt.gensalt());

            try {
                UserMapper.createUser(username, hashedPassword, databaseController);
                ctx.attribute("message", "You are now registered with username: " + username + ". Please log in.");
                ctx.render("index.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Username already exists. Try again or log in.");
                ctx.render("createuser.html");
            }
        } else {
            ctx.attribute("message", "Passwords do not match! Please try again.");
            ctx.render("createuser.html");
        }
    }

    private static void login(Context ctx, DatabaseController databaseController) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, databaseController);

            // Check if the password matches
            if (BCrypt.checkpw(password, user.getPassword())) {
                ctx.sessionAttribute("currentUser", user);
                ctx.attribute("message", "You are now logged in.");
                ctx.render("index.html");
            } else {
                // If the password doesn't match
                ctx.attribute("message", "Invalid password. Please try again.");
                ctx.render("login.html");
            }
        } catch (DatabaseException e) {
            // Display user-friendly error
            ctx.attribute("message", e.getMessage().contains("Bruger ikke fundet")
                    ? "Username not found. Please try again."
                    : "An error occurred during login. Please try again later.");
            ctx.render("login.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
