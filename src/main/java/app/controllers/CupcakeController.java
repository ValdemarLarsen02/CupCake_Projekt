package app.controllers;

import app.entities.Customer;
import app.entities.User;
import app.models.OrderLine;
import app.services.CupcakeService;
import io.javalin.Javalin;

import java.util.List;

public class CupcakeController {

    private static final CupcakeService cupcakeService = CupcakeService.getInstance();

    public static void registerRoutes(Javalin app) {
        app.get("/", ctx -> {
            List<String> bottoms = cupcakeService.getBottoms();
            List<String> toppings = cupcakeService.getToppings();

            ctx.attribute("bottoms", bottoms);
            ctx.attribute("toppings", toppings);
            ctx.render("index.html");
        });

        app.post("/add-to-cart", ctx -> {
            String selectedBottom = ctx.formParam("bottom");
            String selectedTopping = ctx.formParam("topping");
            int quantity = Integer.parseInt(ctx.formParam("quantity"));
            if (selectedBottom == null || selectedTopping == null) {
                ctx.status(400).result("Du skal vælge både en bund og topping.");
                return;
            }

            // Hent userId og userName fra sessionen, eller brug standardværdier for gæstebruger
            Object sessionUser = ctx.sessionAttribute("currentUser");

            if (sessionUser instanceof User user) {
                Integer userId = user.getUserId();
                String customerName = user.getUserName();
                boolean isPaid = false;
                cupcakeService.addToCart(userId, customerName, selectedBottom, selectedTopping, quantity, isPaid);
            } else {
                boolean isPaid = false;
                cupcakeService.addToCart(-1, "Gæst", selectedBottom, selectedTopping, quantity, isPaid);

            }
            ctx.redirect("/cart");
        });


        app.get("/cart", ctx -> {
            List<OrderLine> cartItems = cupcakeService.getCartItems();
            double totalPrice = cupcakeService.calculateTotalPrice();
            ctx.attribute("cartItems", cartItems);
            ctx.attribute("totalPrice", totalPrice);
            ctx.render("basket.html");
        });

        app.post("/remove-from-cart", ctx -> {
            String orderLineIdParam = ctx.formParam("orderLineId");

            if (orderLineIdParam == null) {
                ctx.status(400).result("Order Line id mangler.");
                return;
            }

            int orderLineId = Integer.parseInt(orderLineIdParam);
            cupcakeService.removeFromCart(orderLineId);
            ctx.redirect("/cart");
        });
    }
}
