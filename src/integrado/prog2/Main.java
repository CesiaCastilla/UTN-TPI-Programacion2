package integrado.prog2;


import integrado.prog2.menu.MenuProducto;
import integrado.prog2.menu.MenuUsuario;
import integrado.prog2.menu.MenuPedido;
import integrado.prog2.repository.CategoriaRepository;
import integrado.prog2.repository.PedidoRepository;
import integrado.prog2.repository.ProductoRepository;
import integrado.prog2.repository.UsuarioRepository;
import integrado.prog2.service.ProductoService;
import integrado.prog2.service.UsuarioService;
import java.util.Scanner;

/**
 *
 * @author Alfredo Castillo / Cesia Castilla
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        CategoriaRepository categoriaRepository = new CategoriaRepository();
        ProductoRepository productoRepository = new ProductoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();
        
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        ProductoService productoService = new ProductoService(productoRepository, categoriaRepository);

        MenuProducto menuProducto = new MenuProducto(scanner, categoriaRepository, productoRepository);
        MenuUsuario menuUsuario = new MenuUsuario(scanner, usuarioRepository);
        MenuPedido menuPedido = new MenuPedido(scanner, pedidoRepository, usuarioService, productoService);

        System.out.println("=== BIENVENIDO A FOOD STORE ===");

        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENÚ PRINCIPAL ==========");
            System.out.println("1. Gestión de Categorías (módulo en integración)");
            System.out.println("2. Gestión de Productos");
            System.out.println("3. Gestión de Usuarios");
            System.out.println("4. Gestión de Pedidos (módulo en integración)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> System.out.println("El módulo de Categorías será integrado próximamente.");
                case "2" -> menuProducto.mostrar();
                case "3" -> menuUsuario.mostrar();
                case "4" -> menuPedido.mostrar();
                case "0" -> salir = true;
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }

        System.out.println("\n¡Gracias por usar Food Store!");
        scanner.close();
    }
}
