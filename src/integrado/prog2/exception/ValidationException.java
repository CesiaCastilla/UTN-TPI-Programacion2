
package integrado.prog2.exception;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class ValidationException extends RuntimeException {
    // Constructor que recibe un mensaje personalizado
    public ValidationException(String mensaje) {
        super(mensaje);
    }
}
