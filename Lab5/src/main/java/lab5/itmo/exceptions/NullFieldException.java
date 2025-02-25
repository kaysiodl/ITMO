package lab5.itmo.exceptions;

public class NullFieldException extends Exception{
    public NullFieldException(String field) {
        super(field + "can't be null");
    }
}
