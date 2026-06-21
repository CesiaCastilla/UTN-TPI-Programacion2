package integrado.prog2;


import integrado.prog2.config.DataInitializer;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
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
        MenuProducto menuProducto = new MenuProducto(scanner, categoriaRepository, productoRepository);
        MenuUsuario menuUsuario = new MenuUsuario(scanner, usuarioRepository);
        MenuPedido menuPedido = new MenuPedido(scanner, pedidoRepository, usuarioService, productoService);

        System.out.println("=== BIENVENIDO A FOOD STORE ===");
        
        // =========================================================================
        // SISTEMA DE AUTENTICACIÓN EXPRESS
        // =========================================================================
        Usuario usuarioLogueado = null;
        boolean autenticado = false;
        
        while (!autenticado) {
            System.out.println("\n--- INICIO DE SESIÓN ---");
            System.out.print("Ingrese Mail: ");
            String mail = scanner.nextLine().trim();
            System.out.print("Ingrese Contraseña: ");
            String contrasena = scanner.nextLine().trim();
            
            // Buscar coincidencia exacta por mail y contraseña
            Usuario user = usuarioRepository.listarTodos().stream()
                    .filter(u -> u.getMail().equalsIgnoreCase(mail) && u.getContrasena().equals(contrasena))
                    .findFirst()
                    .orElse(null);
            
            if (user != null) {
                usuarioLogueado = user;
                autenticado = true;
                System.out.println("\n¡Ingreso exitoso! Bienvenido: " + user.getNombre() + " (" + user.getRol() + ")");
            } else {
                System.out.println("Credenciales incorrectas. Intente de nuevo.");
            }
        }
        // =========================================================================

        // Menú Principal controlado por Roles
        boolean salir = false;
        while (!salir) {
            System.out.println("\n========== MENÚ PRINCIPAL ==========");
            
            // Filtro visual según Rol en consola
            if (usuarioLogueado.getRol() == Rol.ADMIN) {
                System.out.println("1. Gestión de Categorías (módulo en integración)");
                System.out.println("2. Gestión de Productos");
                System.out.println("3. Gestión de Usuarios");
            }
            
            // El rol USUARIO común solo ve la gestión de sus pedidos
            System.out.println("4. Gestión de Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> {
                    if (usuarioLogueado.getRol() == Rol.ADMIN) {
                        System.out.println("El módulo de Categorías será integrado próximamente.");
                    } else {
                        System.out.println("Opción inválida para su nivel de acceso.");
                    }
                }
                case "2" -> {
                    if (usuarioLogueado.getRol() == Rol.ADMIN) {
                        menuProducto.mostrar();
                    } else {
                        System.out.println("Opción inválida para su nivel de acceso.");
                    }
                }
                case "3" -> {
                    if (usuarioLogueado.getRol() == Rol.ADMIN) {
                        menuUsuario.mostrar();
                    } else {
                        System.out.println("Opción inválida para su nivel de acceso.");
                    }
                }
                case "4" -> menuPedido.mostrar();
                case "0" -> salir = true;
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }

        System.out.println("\n¡Gracias por usar Food Store!");
        scanner.close();
    }
}
