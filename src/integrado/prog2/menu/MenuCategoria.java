
package integrado.prog2.menu;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.ProductoRepository;
import integrado.prog2.service.CategoriaService;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class MenuCategoria {

    private final Scanner scanner;
    private final CategoriaService categoriaService;

    public MenuCategoria(Scanner scanner, CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.scanner = scanner;
        this.categoriaService = new CategoriaService(categoriaRepository, productoRepository);
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- GESTIÓN DE CATEGORÍAS ---");
            System.out.println("1. Listar categorías");
            System.out.println("2. Crear categoría");
            System.out.println("3. Editar categoría");
            System.out.println("4. Eliminar categoría");
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
        List<Categoria> categorias = categoriaService.listar();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías cargadas");
        } else {
            for (Categoria c : categorias) {
                System.out.println(" -> " + c);
            }
        }
    }

    private void crear() {
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            Categoria categoria = categoriaService.crear(nombre, descripcion);
            System.out.println("Categoría creada con éxito. ID generado: " + categoria.getId());
        } catch (ValidationException e) {
            System.out.println("Error al crear la categoría: " + e.getMessage());
        }
    }

    private void editar() {
        Long id = leerLong("Ingrese el ID de la categoría a editar: ");
        try {
            Categoria actual = categoriaService.obtenerPorId(id);
            System.out.println("Editando: " + actual);
            System.out.println("Deje el campo vacío para mantener el valor actual.");

            System.out.print("Nombre [" + actual.getNombre() + "]: ");
            String nombreInput = scanner.nextLine().trim();
            String nombre = nombreInput.isEmpty() ? actual.getNombre() : nombreInput;

            System.out.print("Descripción [" + actual.getDescripcion() + "]: ");
            String descripcionInput = scanner.nextLine().trim();
            String descripcion = descripcionInput.isEmpty() ? actual.getDescripcion() : descripcionInput;

            categoriaService.editar(id, nombre, descripcion);
            System.out.println("Categoría actualizada con éxito.");
        } catch (EntidadNoEncontradaException | ValidationException e) {
            System.out.println("Error al editar la categoría: " + e.getMessage());
        }
    }

    private void eliminar() {
        Long id = leerLong("Ingrese el ID de la categoría a eliminar: ");

        if (categoriaService.tieneProductosAsociados(id)) {
            System.out.println("Atención: esta categoría tiene productos asociados. Se eliminará igualmente, "
                    + "pero esos productos conservarán la referencia a una categoría dada de baja.");
        }

        System.out.print("¿Confirma la eliminación de la categoría ID " + id + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim();

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            categoriaService.eliminar(id);
            System.out.println("Categoría eliminada con éxito (baja lógica).");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error al eliminar la categoría: " + e.getMessage());
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
}
