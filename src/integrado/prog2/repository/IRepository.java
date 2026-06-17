
package integrado.prog2.repository;

import java.util.List;
/**
 *
 * @author macbook
 */

public interface IRepository<T> {
    void guardar(T entidad);
    List<T> listarTodos();
    T buscarPorId(Long id);
    void eliminar(Long id); // Baja lógica
}
