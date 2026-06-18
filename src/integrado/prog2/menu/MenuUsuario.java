
package integrado.prog2.menu;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.UsuarioRepository;
import integrado.prog2.service.UsuarioService;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class MenuUsuario {

    private final Scanner scanner;
    private final UsuarioService usuarioService;

    public MenuUsuario(Scanner scanner, UsuarioRepository usuarioRepository) {
        this.scanner = scanner;
        this.usuarioService = new UsuarioService(usuarioRepository);
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- GESTIÓN DE USUARIOS ---");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Editar usuario");
            System.out.println("4. Eliminar usuario");
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
        List<Usuario> usuarios = usuarioService.listar();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados");
        } else {
            for (Usuario u : usuarios) {
                System.out.println(" -> " + u);
            }
        }
    }

    private void crear() {
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine().trim();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine().trim();
            System.out.print("Mail: ");
            String mail = scanner.nextLine().trim();
            System.out.print("Celular: ");
            String celular = scanner.nextLine().trim();
            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine().trim();
            Rol rol = leerRol();

            Usuario usuario = usuarioService.crear(nombre, apellido, mail, celular, contrasena, rol);
            System.out.println("Usuario creado con éxito. ID generado: " + usuario.getId());
        } catch (ValidationException e) {
            System.out.println("Error al crear el usuario: " + e.getMessage());
        }
    }

    private void editar() {
        Long id = leerLong("Ingrese el ID del usuario a editar: ");
        try {
            Usuario actual = usuarioService.obtenerPorId(id);
            System.out.println("Editando: " + actual);
            System.out.println("Deje el campo vacío para mantener el valor actual.");

            System.out.print("Nombre [" + actual.getNombre() + "]: ");
            String nombreInput = scanner.nextLine().trim();
            String nombre = nombreInput.isEmpty() ? actual.getNombre() : nombreInput;

            System.out.print("Apellido [" + actual.getApellido() + "]: ");
            String apellidoInput = scanner.nextLine().trim();
            String apellido = apellidoInput.isEmpty() ? actual.getApellido() : apellidoInput;

            System.out.print("Mail [" + actual.getMail() + "]: ");
            String mailInput = scanner.nextLine().trim();
            String mail = mailInput.isEmpty() ? actual.getMail() : mailInput;

            System.out.print("Celular [" + actual.getCelular() + "]: ");
            String celularInput = scanner.nextLine().trim();
            String celular = celularInput.isEmpty() ? actual.getCelular() : celularInput;

            System.out.print("Contraseña [sin cambios si se deja vacío]: ");
            String contrasenaInput = scanner.nextLine().trim();
            String contrasena = contrasenaInput.isEmpty() ? actual.getContrasena() : contrasenaInput;

            System.out.print("¿Modificar rol actual (" + actual.getRol() + ")? (S/N): ");
            String modificarRol = scanner.nextLine().trim();
            Rol rol = modificarRol.equalsIgnoreCase("S") ? leerRol() : actual.getRol();

            usuarioService.editar(id, nombre, apellido, mail, celular, contrasena, rol);
            System.out.println("Usuario actualizado con éxito.");
        } catch (EntidadNoEncontradaException | ValidationException e) {
            System.out.println("Error al editar el usuario: " + e.getMessage());
        }
    }

    private void eliminar() {
        Long id = leerLong("Ingrese el ID del usuario a eliminar: ");
        System.out.print("¿Confirma la eliminación del usuario ID " + id + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim();

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            usuarioService.eliminar(id);
            System.out.println("Usuario eliminado con éxito (baja lógica).");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    private Rol leerRol() {
        while (true) {
            System.out.println("Rol: 1) ADMIN  2) USUARIO");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1":
                    return Rol.ADMIN;
                case "2":
                    return Rol.USUARIO;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
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
