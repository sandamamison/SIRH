package SI.RH.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entityName, Object id) {
        super(entityName + " introuvable avec l'identifiant : " + id);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
