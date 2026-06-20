package integrado.prog2.menu;


import integrado.prog2.entities.Pedido;
import integrado.prog2.enums.EstadoPedido;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.PedidoRepository;
import integrado.prog2.service.PedidoService;
import integrado.prog2.service.ProductoService;
import integrado.prog2.service.UsuarioService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Rocio Aguero
 * Comision: 6
 * Participante 3
 */
public class MenuPedido {
    
    private final Scanner scanner;
    private final PedidoService pedidoService;
    
    public MenuPedido(Scanner scanner, PedidoRepository pedidoRepository, UsuarioService usuarioService, ProductoService productoService) {
        this.scanner = scanner;
        this.pedidoService = new PedidoService(pedidoRepository, usuarioService, productoService);
    }
    
    public void mostrar(){
        boolean salir = false;
        while (!salir){
            System.out.println("\n--- GESTIÓN DE PEDIDOS ---");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Crear pedido");
            System.out.println("3. Editar pedido (Estado/Forma Pago)");
            System.out.println("4. Eliminar pedido");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opcion: ");
            
            switch(opcion){
                case 1 -> listar();
                case 2 -> crear();
                case 3 -> editar();
                case 4 -> eliminar();
                case 0 -> salir = true;
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }
    
    private void listar(){
        System.out.println("¿Desea filtrar por usuario? (S/N): ");
        String respuesta = scanner.nextLine().trim();
        
        List<Pedido> pedidos;
        if(respuesta.equalsIgnoreCase("S")){
            Long usuarioId = leerLong("Ingrese el ID del usuario: ");
            pedidos = pedidoService.listarPedidos(usuarioId);
        }else{
            pedidos = pedidoService.listarPedidos(null);
        }
        
        if(pedidos.isEmpty()){
            System.out.println("No hay pedidos registrados");
        }else{
            System.out.println("\nListado de Pedidos:");
            for (Pedido p : pedidos) {
                System.out.println("ID: " + p.getId() 
                        + " | Usuario: " + p.getUsuario().getNombre() 
                        + " | Estado: " + p.getEstado() 
                        + " | Forma Pago: " + p.getFormaPago() 
                        + " | Total: $" + p.getTotal() 
                        + " | Fecha: " + p.getFecha());
            }
        }
    }
    
    private void crear(){
        try{
            Long usuarioId = leerLong("ID del usuario que realiza la compra: ");
            System.out.println("Debe seleccionar una forma de pago:");
            FormaPago[] formas = FormaPago.values();
            for (int i = 0; i < formas.length; i++) {
                System.out.println((i + 1) + ". " + formas[i]);
            }
            int opcForma = 0;
            while (opcForma < 1 || opcForma > formas.length){
                opcForma = leerEntero("Opcion: ");
                if(opcForma < 1 || opcForma > formas.length){
                    System.out.println("Error, la opcion es invalida");
                }
            }
            FormaPago formaPago = formas[opcForma - 1];
            
            List<Long> productosIds = new ArrayList<>();
            List<Integer> cantidades = new ArrayList<>();
            
            boolean agregarMas = true;
            System.out.println("\n--- CARGA DE DETALLES ---");
            while (agregarMas) {
                Long prodId = leerLong("Debe ingresar el ID del Producto: ");
                int cantidad = leerEntero("Debe ingresar la Cantidad: ");

                productosIds.add(prodId);
                cantidades.add(cantidad);

                System.out.print("¿Desea agregar algun otro producto al pedido? (S/N): ");
                agregarMas = scanner.nextLine().trim().equalsIgnoreCase("S");
            }
            Pedido pedidoCreado = pedidoService.crearPedido(usuarioId, formaPago, productosIds, cantidades);
            System.out.println("Pedido creado!. ID generado: " + pedidoCreado.getId() + " | Total final: $" + pedidoCreado.getTotal());
        }catch 
                (ValidationException | EntidadNoEncontradaException e) {
            System.out.println("Error al crear el pedido: " + e.getMessage());}
    }
        
    private void editar(){
        Long id = leerLong("Ingrese el ID del pedido a editar: ");
        try {
            System.out.println("Elija la opcion de lo que desea modificar");
            System.out.println("1. Modificar Estado");
            System.out.println("2. Modificar Forma de Pago");
            System.out.println("3. Ambos");
            
            int opcion = 0;
            while(opcion < 1 || opcion >3){
                opcion = leerEntero("Seleccione una opcion: ");
                if(opcion < 1 || opcion >3){
                    System.out.println("Error, debe seleccionar la opcion correcta");
                }
            }
            
            EstadoPedido nvEstado = null;
            FormaPago nvFormaPago = null;
            
            if (opcion == 1 || opcion == 3) {
                System.out.println("\nSeleccione el nuevo Estado:");
                EstadoPedido[] estados = EstadoPedido.values();
                for (int i = 0; i < estados.length; i++) {
                    System.out.println((i + 1) + ". " + estados[i]);
                }
            
                int opcEst = 0;
                while (opcEst < 1 || opcEst > estados.length) {
                    opcEst = leerEntero("Opción: ");
                    if (opcEst < 1 || opcEst > estados.length) {
                        System.out.println("Error, debe elegir la opcion correcta ");
                    }
                }
                nvEstado = estados[opcEst - 1];
            }
            
            if (opcion == 2 || opcion == 3) {
                System.out.println("\nSeleccione la nueva Forma de Pago:");
                FormaPago[] formas = FormaPago.values();
                for (int i = 0; i < formas.length; i++) {
                    System.out.println((i + 1) + ". " + formas[i]);
                }
            
                int opcForma = 0;
                while (opcForma < 1 || opcForma > formas.length) {
                    opcForma = leerEntero("Opción: ");
                    if (opcForma < 1 || opcForma > formas.length) {
                        System.out.println("Error, elija la opcion correcta ");
                    }
                }
                nvFormaPago = formas[opcForma - 1];
            
            }
            pedidoService.actualizarPedido(id, nvEstado, nvFormaPago);
            System.out.println("Pedido actualizado!");
        }catch (EntidadNoEncontradaException e) {
        System.out.println("Error al editar el pedido: " + e.getMessage());
        }
    }    
    
    private void eliminar(){
        Long id = leerLong("Ingrese el ID del pedido a eliminar: ");
        System.out.print("¿Esta seguro que desea eliminar este pedido? (S/N): ");
        String confirmacion = scanner.nextLine().trim();

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Cancelado");
            return;
        }

        try {
            pedidoService.eliminarPedido(id);
            System.out.println("Pedido eliminado");
        } catch (EntidadNoEncontradaException e) {
            System.out.println("Error al eliminar el pedido: " + e.getMessage());
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
