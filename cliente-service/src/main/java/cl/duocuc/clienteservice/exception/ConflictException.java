package cl.duocuc.clienteservice.exception;

/**
 * Excepción lanzada cuando hay un conflicto con los datos existentes.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

