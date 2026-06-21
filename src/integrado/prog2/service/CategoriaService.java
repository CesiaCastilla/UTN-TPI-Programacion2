
package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.ProductoRepository;
import java.util.List;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public Categoria crear(String nombre, String descripcion) {
        validarNombre(nombre, null);

        Categoria categoria = new Categoria(nombre, descripcion);
        categoriaRepository.guardar(categoria);
        return categoria;
    }

    public Categoria editar(Long id, String nombre, String descripcion) {
        Categoria existente = obtenerPorId(id);
        validarNombre(nombre, id);

        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);

        categoriaRepository.guardar(existente);
        return existente;
    }

    public void eliminar(Long id) {
        categoriaRepository.eliminar(id);
    }

    // Regla de cátedra: la baja se permite igual, pero se informa si hay productos asociados.
    public boolean tieneProductosAsociados(Long categoriaId) {
        for (Producto p : productoRepository.listarTodos()) {
            if (p.getCategoria() != null && p.getCategoria().getId().equals(categoriaId)) {
                return true;
            }
        }
        return false;
    }

    public List<Categoria> listar() {
        return categoriaRepository.listarTodos();
    }

    public Categoria obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.buscarPorId(id);
        if (categoria == null) {
            throw new EntidadNoEncontradaException("No existe una categoría activa con ID " + id);
        }
        return categoria;
    }

    private void validarNombre(String nombre, Long idAEditar) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidationException("El nombre de la categoría no puede estar vacío");
        }
        for (Categoria c : categoriaRepository.listarTodos()) {
            boolean esLaMisma = idAEditar != null && c.getId().equals(idAEditar);
            if (!esLaMisma && c.getNombre().equalsIgnoreCase(nombre)) {
                throw new ValidationException("Ya existe una categoría registrada con el nombre " + nombre);
            }
        }
    }
}
