package integrado.prog2;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.repository.CategoriaRepository;
import java.util.List;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BIENVENIDO A FOOD STORE - TEST DE MEMORIA ===");
        
        // 1. Instanciamos tu repositorio de categorías
        CategoriaRepository repo = new CategoriaRepository();

        // 2. PROBAMOS EL GUARDAR (Alta)
        System.out.println("\n1. Guardando categorías de prueba...");
        Categoria cat1 = new Categoria("Hamburguesas");
        Categoria cat2 = new Categoria("Pizzas y Empanadas");
        Categoria cat3 = new Categoria("Bebidas");
        
        repo.guardar(cat1);
        repo.guardar(cat2);
        repo.guardar(cat3);
        System.out.println("¡Categorías guardadas con éxito!");

        // 3. PROBAMOS EL LISTAR
        System.out.println("\n2. Listando todas las categorías activas:");
        mostrarCategorias(repo.listarTodos());

        // 4. PROBAMOS EL UPDATE (Modificar el nombre de la categoría ID 2)
        System.out.println("\n3. Modificando el nombre de la categoría ID 2...");
        Categoria catAEditar = repo.buscarPorId(2L);
        if (catAEditar != null) {
            catAEditar.setNombre("Pizzas y Empanadas Gourmet");
            repo.guardar(catAEditar); // Al tener ID asignado, hace un Update en la lista
        }
        
        System.out.println("Lista actualizada:");
        mostrarCategorias(repo.listarTodos());

        // 5. PROBAMOS EL ELIMINAR (Baja lógica)
        System.out.println("\n4. Aplicando baja lógica a la categoría ID 3 (Bebidas)...");
        repo.eliminar(3L);

        System.out.println("Lista final (Bebidas no debería aparecer):");
        mostrarCategorias(repo.listarTodos());
        
        System.out.println("\n=================================================");
        System.out.println("¡TEST COMPLETADO CON ÉXITO! Todo funciona de diez.");
        System.out.println("=================================================");
        
        
        // 6. PROBAMOS LANZAR LA EXCEPCIÓN PERSONALIZADA
        System.out.println("\n5. Intentando eliminar un ID inexistente (ID 99) para forzar error...");
        try {
            repo.eliminar(99L); // Esto debería hacer saltar tu excepción
        } catch (EntidadNoEncontradaException e) {
            System.out.println("❌ EXCEPCIÓN CAPTURADA EXITOSAMENTE: " + e.getMessage());
        }
    }

    // Método auxiliar para imprimir limpio por consola
    private static void mostrarCategorias(List<Categoria> lista) {
        if (lista.isEmpty()) {
            System.out.println("(No hay categorías activas)");
        } else {
            for (Categoria c : lista) {
                System.out.println(" -> " + c.toString());
            }
        }
    }
}
