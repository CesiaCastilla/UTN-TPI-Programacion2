
package integrado.prog2.repository;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class CategoriaRepository implements IRepository<Categoria> {
    
    // Nuestra "Base de Datos" en memoria
    private static final List<Categoria> tablaCategorias = new ArrayList<>();
    private static Long ultimoId = 0L;
    
    @Override
    public void guardar(Categoria categoria) {
        if (categoria.getId() == null) {
            // Es una nueva categoría (Insert)
            ultimoId++;
            categoria.setId(ultimoId);
            tablaCategorias.add(categoria);
        } else {
            // Es una actualización (Update)
            Categoria existente = buscarPorId(categoria.getId());
            if (existente != null) {
                existente.setNombre(categoria.getNombre());
                existente.setDescripcion(categoria.getDescripcion());
            }
        }
    }
    
    @Override
    public List<Categoria> listarTodos() {
        List<Categoria> activas = new ArrayList<>();
        for (Categoria cat : tablaCategorias) {
            if (!cat.isEliminado()) { // Solo mostramos las que no tienen baja lógica
                activas.add(cat);
            }
        }
        return activas;
    }
    
    @Override
    public Categoria buscarPorId(Long id) {
        for (Categoria cat : tablaCategorias) {
            if (cat.getId().equals(id) && !cat.isEliminado()) {
                return cat;
            }
        }
        return null;
    }
    
    @Override
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
    
        // SI NO EXISTE, LANZAMOS NUESTRA EXCEPCIÓN PERSONALIZADA:
        if (categoria == null) {
            throw new EntidadNoEncontradaException("Error al eliminar: No existe la categoría con ID " + id);
        }
    
        // Si existe, aplicamos la baja lógica
        categoria.setEliminado(true);
    }
}
