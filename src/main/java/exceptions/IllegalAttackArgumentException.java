package exceptions;

public class IllegalAttackArgumentException extends RuntimeException {
    public IllegalAttackArgumentException(String message) {
        super(message);
    }
}
