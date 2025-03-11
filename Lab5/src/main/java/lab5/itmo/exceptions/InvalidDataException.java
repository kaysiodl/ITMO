package lab5.itmo.exceptions;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String data) {
        super("Data was entered incorrectly. Please try again.");
    }
}
