package lab5.itmo.exceptions;

public class ExecutionError extends RuntimeException {
    public ExecutionError() {
        super("Error during command execution.");
    }

    public ExecutionError(String message) {
        super(message);
    }
}
