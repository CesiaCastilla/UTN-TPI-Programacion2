
package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.ProductoRepository;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Producto crear(String nombre, Double precio, String descripcion, int stock, String imagen, Boolean disponible, Long categoriaId) {
        validarPrecioYStock(precio, stock);
        Categoria categoria = obtenerCategoriaValida(categoriaId);

        Producto producto = new Producto(nombre, precio, descripcion, stock, imagen, disponible, categoria);
        productoRepository.guardar(producto);
        return producto;
    }

    public Producto editar(Long id, String nombre, Double precio, String descripcion, int stock, String imagen, Boolean disponible, Long categoriaId) {
        Producto existente = obtenerPorId(id);
        validarPrecioYStock(precio, stock);
        Categoria categoria = obtenerCategoriaValida(categoriaId);

        existente.setNombre(nombre);
        existente.setPrecio(precio);
        existente.setDescripcion(descripcion);
        existente.setStock(stock);
        existente.setImagen(imagen);
        existente.setDisponible(disponible);
        existente.setCategoria(categoria);

        productoRepository.guardar(existente);
        return existente;
    }

    public void eliminar(Long id) {
        productoRepository.eliminar(id);
    }

    public List<Producto> listar() {
        return productoRepository.listarTodos();
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productoRepository.listarTodos()) {
            if (p.getCategoria() != null && p.getCategoria().getId().equals(categoriaId)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Producto obtenerPorId(Long id) {
        Producto producto = productoRepository.buscarPorId(id);
        if (producto == null) {
            throw new EntidadNoEncontradaException("No existe un producto activo con ID " + id);
        }
        return producto;
    }

    private Categoria obtenerCategoriaValida(Long categoriaId) {
        Categoria categoria = categoriaRepository.buscarPorId(categoriaId);
        if (categoria == null) {
            throw new EntidadNoEncontradaException("No existe una categoría activa con ID " + categoriaId);
        }
        return categoria;
    }

    private void validarPrecioYStock(Double precio, int stock) {
        if (precio == null || precio < 0) {
            throw new StockInvalidoException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new StockInvalidoException("El stock no puede ser negativo");
        }
    }
}
