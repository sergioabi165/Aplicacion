package mx.edu.inventario.exception;

/** Se lanza al incumplir una regla de negocio o validación. */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
