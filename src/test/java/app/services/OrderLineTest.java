package app.services;

import app.models.OrderLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderLineTest {

    @Test
    void testOrderLineCreation() {
        OrderLine orderLine = new OrderLine("Chocolate", "Vanilla", 2, 20.0);

        assertEquals("Chocolate", orderLine.getBottom());
        assertEquals("Vanilla", orderLine.getTopping());
        assertEquals(2, orderLine.getQuantity());
        assertEquals(20.0, orderLine.getTotalPrice(), 0.01);
    }

    @Test
    void testIdIncrement() {
        OrderLine firstOrderLine = new OrderLine("Chocolate", "Strawberry", 1, 10.0);
        OrderLine secondOrderLine = new OrderLine("Vanilla", "Caramel", 2, 15.0);

        assertEquals(firstOrderLine.getId() + 1, secondOrderLine.getId(),
                "ID'et for den anden ordrelinje skal være en højere end den første.");
    }
}
