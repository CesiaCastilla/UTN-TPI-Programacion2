
package integrado.prog2.repository;

import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class ProductoRepository implements IRepository<Producto> {

    // Nuestra "Base de Datos" en memoria
    private static final List<Producto> tablaProductos = new ArrayList<>();
    private static Long ultimoId = 0L;

    @Override
    public void guardar(Producto producto) {
        if (producto.getId() == null) {
            // Es un nuevo producto (Insert)
            ultimoId++;
            producto.setId(ultimoId);
            tablaProductos.add(producto);
        } else {
            // Es una actualización (Update)
            Producto existente = buscarPorId(producto.getId());
            if (existente != null) {
                existente.setNombre(producto.getNombre());
                existente.setPrecio(producto.getPrecio());
                existente.setDescripcion(producto.getDescripcion());
                existente.setStock(producto.getStock());
                existente.setImagen(producto.getImagen());
                existente.setDisponible(producto.getDisponible());
                existente.setCategoria(producto.getCategoria());
            }
        }
    }

    @Override
    public List<Producto> listarTodos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto p : tablaProductos) {
            if (!p.isEliminado()) { // Solo mostramos los que no tienen baja lógica
                activos.add(p);
            }
        }
        return activos;
    }

    @Override
    public Producto buscarPorId(Long id) {
        for (Producto p : tablaProductos) {
            if (p.getId().equals(id) && !p.isEliminado()) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);

        if (producto == null) {
            throw new EntidadNoEncontradaException("Error al eliminar: No existe el producto con ID " + id);
        }

        // Baja lógica: nunca se remueve de la colección porque puede estar referenciado en detalles de pedidos
        producto.setEliminado(true);
    }
}
