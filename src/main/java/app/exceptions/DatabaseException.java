package app.exceptions;

public class DatabaseException extends Exception {
    public DatabaseException(String userMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
    }
}