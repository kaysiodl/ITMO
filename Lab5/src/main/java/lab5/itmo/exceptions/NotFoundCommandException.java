package lab5.itmo.exceptions;

public class NotFoundCommandException extends RuntimeException {
    public NotFoundCommandException(String commandName) {
        super("Command " + commandName + " is not found." );
    }
}
