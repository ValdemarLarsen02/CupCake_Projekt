package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.controllers.DatabaseController;
import app.persistence.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private DatabaseController databaseController;

    @BeforeEach
    public void setUp() {
        // Initialize your DatabaseController, possibly with a test database
        databaseController = new DatabaseController(); // Adjust as needed
    }

    @Test
    public void testLoginSuccessful() {
        String username = "Jonas1234"; // Ensure this user exists in your test DB
        String password = "$2a$10$SoIZARzCG8AdccJ..N5Piu5t8t6t28Vq7OyHsPY7C7IlIJPmSlVwy"; // Ensure the correct password is used

        try {
            User user = UserMapper.login(username, databaseController);
            assertNotNull(user, "User should not be null");
            assertEquals(username, user.getUserName());
        } catch (DatabaseException e) {
            fail("Login should succeed but threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testLoginFailed_UserNotFound() {
        String username = "nonExistentUser"; // This user should not exist
        String password = "somePassword";

        Exception exception = assertThrows(DatabaseException.class, () -> {
            UserMapper.login(username, databaseController);
        });

        assertEquals("Fejl i login. Prøv igen", exception.getMessage());
    }
}