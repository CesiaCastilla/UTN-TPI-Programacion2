
package integrado.prog2.config;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.ProductoRepository;
import integrado.prog2.repository.UsuarioRepository;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class DataInitializer {
    public static void cargarDatosIniciales(
            CategoriaRepository catRepo, 
            ProductoRepository prodRepo, 
            UsuarioRepository userRepo) {
        // 1. Precargar Categoría base de contingencia (ID 1)
        if (catRepo.listarTodos().isEmpty()) {
            Categoria cat = new Categoria();
            cat.setNombre("Comidas Rápidas");
            cat.setDescripcion("Hamburguesas, pizzas y minutas");
            catRepo.guardar(cat);
        }
        
        // 2. Precargar Usuarios de prueba
        if (userRepo.listarTodos().isEmpty()) {
            Usuario admin = new Usuario("Alfredo", "Castillo", "admin@foodstore.com", "11223344", "1234", Rol.ADMIN);
            Usuario cliente = new Usuario("Alex", "Belisario", "usuario@foodstore.com", "11556677", "1234", Rol.USUARIO);
            
            userRepo.guardar(admin);
            userRepo.guardar(cliente);
        }
    }
}
