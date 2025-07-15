package exceptions;

public class EcosystemNotFoundException extends RuntimeException {
    public EcosystemNotFoundException(String message) {
        super(message);
    }
}
