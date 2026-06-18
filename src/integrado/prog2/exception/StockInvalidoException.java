
package integrado.prog2.exception;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class StockInvalidoException extends RuntimeException {
    // Constructor que recibe un mensaje personalizado
    public StockInvalidoException(String mensaje) {
        super(mensaje);
    }
}
