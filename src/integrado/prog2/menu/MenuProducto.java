
package integrado.prog2.menu;

import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.ProductoRepository;
import integrado.prog2.service.ProductoService;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class MenuProducto {

    private final Scanner scanner;
    private final ProductoService productoService;

    public MenuProducto(Scanner scanner, CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.scanner = scanner;
        this.productoService = new ProductoService(productoRepository, categoriaRepository);
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Listar productos");
            System.out.println("2. Crear producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> listar();
                case 2 -> crear();
                case 3 -> editar();
                case 4 -> eliminar();
                case 0 -> salir = true;
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private void listar() {
        System.out.print("¿Desea filtrar por categoría? (S/N): ");
        String respuesta = scanner.nextLine().trim();

        List<Producto> productos;
        if (respuesta.equalsIgnoreCase("S")) {
            Long categoriaId = leerLong("Ingrese el ID de la categoría: ");
            productos = productoService.listarPorCategoria(categoriaId);
        } else {
            productos = productoService.listar();
        }

        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados");
        } else {
            for (Producto p : productos) {
                System.out.println(" -> " + p);
            }
        }
    }

    private void crear() {
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();
            Double precio = leerDouble("Precio: ");
            int stock = leerEntero("Stock: ");
            System.out.print("Imagen (URL o nombre de archivo): ");
            String imagen = scanner.nextLine().trim();
            System.out.print("¿Disponible? (S/N): ");
            boolean disponible = scanner.nextLine().trim().equalsIgnoreCase("S");
            Long categoriaId = leerLong("ID de la categoría: ");

            Producto producto = productoService.crear(nombre, precio, descripcion, stock, imagen, disponible, categoriaId);
            System.out.println("Producto creado con éxito. ID generado: " + producto.getId());
        } catch (StockInvalidoException | EntidadNoEncontradaException | ValidationException e) {
            System.out.println("Error al crear el producto: " + e.getMessage());
        }
    }

    private void editar() {
        Long id = leerLong("Ingrese el ID del producto a editar: ");
        try {
            Producto actual = productoService.obtenerPorId(id);
            System.out.println("Editando: " + actual);
            System.out.println("Deje el campo vacío para mantener el valor actual.");

            System.out.print("Nombre [" + actual.getNombre() + "]: ");
            String nombreInput = scanner.nextLine().trim();
            String nombre = nombreInput.isEmpty() ? actual.getNombre() : nombreInput;

            System.out.print("Descripción [" + actual.getDescripcion() + "]: ");
            String descripcionInput = scanner.nextLine().trim();
            String descripcion = descripcionInput.isEmpty() ? actual.getDescripcion() : descripcionInput;

            System.out.print("Precio [" + actual.getPrecio() + "]: ");
            String precioInput = scanner.nextLine().trim();
            Double precio = precioInput.isEmpty() ? actual.getPrecio() : Double.parseDouble(precioInput);

            System.out.print("Stock [" + actual.getStock() + "]: ");
            String stockInput = scanner.nextLine().trim();
            int stock = stockInput.isEmpty() ? actual.getStock() : Integer.parseInt(stockInput);

            System.out.print("Imagen [" + actual.getImagen() + "]: ");
            String imagenInput = scanner.nextLine().trim();
            String imagen = imagenInput.isEmpty() ? actual.getImagen() : imagenInput;

            System.out.print("¿Disponible? (S/N) [" + (Boolean.TRUE.equals(actual.getDisponible()) ? "S" : "N") + "]: ");
            String dispInput = scanner.nextLine().trim();
            boolean disponible = dispInput.isEmpty() ? Boolean.TRUE.equals(actual.getDisponible()) : dispInput.equalsIgnoreCase("S");

            Long categoriaActualId = actual.getCategoria() != null ? actual.getCategoria().getId() : null;
            System.out.print("ID de categoría [" + categoriaActualId + "]: ");
            String catInput = scanner.nextLine().trim();
            Long categoriaId = catInput.isEmpty() ? categoriaActualId : Long.parseLong(catInput);

            productoService.editar(id, nombre, precio, descripcion, stock, imagen, disponible, categoriaId);
            System.out.println("Producto actualizado con éxito.");
        } catch (EntidadNoEncontradaException | StockInvalidoException | ValidationException e) {
            System.out.println("Error al editar el producto: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un valor numérico válido.");
        }
    }

    private void eliminar() {
        Long id = leerLong("Ingrese el ID del producto a eliminar: ");
        System.out.print("¿Confirma la eliminación del producto ID " + id + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim();

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            productoService.eliminar(id);
            System.out.println("Producto eliminado con éxito (baja lógica).");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error al eliminar el producto: " + e.getMessage());
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    private Long leerLong(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    private Double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número (use punto decimal).");
            }
        }
    }
}
