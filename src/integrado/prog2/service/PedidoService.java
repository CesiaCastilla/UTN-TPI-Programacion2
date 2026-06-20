
package integrado.prog2.service;

import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.EstadoPedido;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.PedidoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rocio Aguero
 * Comision: 6
 * Participante 3
 */
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioService usuarioService, ProductoService productoService) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }
    
    public List<Pedido> listarPedidos(Long usuarioId){
        List<Pedido> activo = pedidoRepository.listarTodos();
        List<Pedido> filtrados = new ArrayList<>();
        for(Pedido pedido : activo){
            if(usuarioId == null || pedido.getUsuario() != null && pedido.getUsuario().getId().equals(usuarioId)){
                filtrados.add(pedido);
            }
        }
        return filtrados;
    }
    
    public Pedido crearPedido(Long usuarioId, FormaPago formaPago, List<Long> productosIds, List<Integer> cantidades){
        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        if(usuario == null || usuario.isEliminado()){
            throw new ValidationException("El usuario no existe");
        }
        
        if (productosIds == null || productosIds.isEmpty()) {
            throw new ValidationException("Debe agregar al menos un producto al pedido.");
        }
        
       for(int i=0; i<productosIds.size(); i++){
            Long prodId = productosIds.get(i);
            int cantidad = cantidades.get(i);
                
            Producto producto = productoService.obtenerPorId(prodId);
            if(producto == null || producto.isEliminado()){
                throw new EntidadNoEncontradaException("El producto no existe");
            }
            if(producto.getStock() < cantidad){
                throw new ValidationException("No hay stock suficiente");
            }
                 
       }
       
            
       Pedido nvPedido = new Pedido();
       nvPedido.setFecha(LocalDate.now());
       nvPedido.setEstado(EstadoPedido.PENDIENTE);
       nvPedido.setFormaPago(formaPago);
       nvPedido.setUsuario(usuario);
       
       List<Producto> productosModificados = new ArrayList<>();
       List<Integer> cantidadesRestadas = new ArrayList<>();
       
       try{
           for(int i = 0; i < productosIds.size(); i++){
                Long prodId = productosIds.get(i);
                int cantidad = cantidades.get(i);

                Producto producto = productoService.obtenerPorId(prodId);

                producto.setStock(producto.getStock()-cantidad);
                productosModificados.add(producto);
                cantidadesRestadas.add(cantidad);

                nvPedido.addDetallePedido(cantidad, producto.getPrecio(), producto);
           }
           
            pedidoRepository.guardar(nvPedido);
            return nvPedido;
            
        }catch(Exception e){
            for(int i=0; i <productosModificados.size();i++){
                Producto prod = productosModificados.get(i);
                int cant = cantidadesRestadas.get(i);
                prod.setStock(prod.getStock() + cant); 
            }
            throw e;
             
        }
        
    }

    
    public void actualizarPedido(Long pedidoId, EstadoPedido nvEstado, FormaPago nvFormaPago){
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if(pedido == null){
            throw new EntidadNoEncontradaException("El pedido no existe");
        }
        
        if(nvEstado != null){
            pedido.setEstado(nvEstado);
        }
        if(nvFormaPago != null){
            pedido.setFormaPago(nvFormaPago);
        }
        
        pedidoRepository.guardar(pedido);
    }
    
    public void eliminarPedido(Long pedidoId){
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if(pedido == null){
            throw new EntidadNoEncontradaException("El pedido no existe");
        }
        
        if(pedido.getDetalles() != null){
           for(DetallePedido detalle : pedido.getDetalles()){
                detalle.setEliminado(true);
           }
        }
        
        pedidoRepository.eliminar(pedidoId);
        
       
    }
    
}
