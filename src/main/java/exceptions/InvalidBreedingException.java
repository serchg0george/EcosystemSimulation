package exceptions;

public class InvalidBreedingException extends RuntimeException {
    public InvalidBreedingException(String message) {
        super(message);
    }
}
