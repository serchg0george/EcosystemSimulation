package exceptions;

public class IllegalAttackTargetException extends RuntimeException {
    public IllegalAttackTargetException(String message) {
        super(message);
    }
}
