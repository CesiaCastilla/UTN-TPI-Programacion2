
package integrado.prog2.exception;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class EntidadNoEncontradaException extends RuntimeException{
    // Constructor que recibe un mensaje personalizado
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
