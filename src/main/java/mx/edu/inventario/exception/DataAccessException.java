package mx.edu.inventario.exception;

/** Representa una falla controlada de conexión o persistencia. */
public class DataAccessException extends Exception {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
