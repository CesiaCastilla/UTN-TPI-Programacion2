package integrado.prog2;


import integrado.prog2.config.DataInitializer;
import integrado.prog2.menu.MenuCategoria;
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
 * @author Alfredo Castillo / Cesia Castilla / Rocio Aguero
 * Comisiones: 3, 5 y 6
 * TPI Programación 2
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Instanciación de Repositorios
        CategoriaRepository categoriaRepository = new CategoriaRepository();
        ProductoRepository productoRepository = new ProductoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();

        // Inyección de dependencias en Servicios
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        ProductoService productoService = new ProductoService(productoRepository, categoriaRepository);

        // Inicialización de datos en memoria para la demo
        DataInitializer.cargarDatosIniciales(categoriaRepository, productoRepository, usuarioRepository);

        // Instanciación de Menús
        MenuCategoria menuCategoria = new MenuCategoria(scanner, categoriaRepository, productoRepository);
        MenuProducto menuProducto = new MenuProducto(scanner, categoriaRepository, productoRepository);
        MenuUsuario menuUsuario = new MenuUsuario(scanner, usuarioRepository);
        MenuPedido menuPedido = new MenuPedido(scanner, pedidoRepository, usuarioService, productoService);

        System.out.println("=== BIENVENIDO A FOOD STORE ===");

        // El acceso es directo a través del menú, sin login (según consigna).
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENÚ PRINCIPAL ==========");
            System.out.println("1. Gestión de Categorías");
            System.out.println("2. Gestión de Productos");
            System.out.println("3. Gestión de Usuarios");
            System.out.println("4. Gestión de Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> menuCategoria.mostrar();
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
