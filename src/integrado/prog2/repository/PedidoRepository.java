
package integrado.prog2.repository;

import integrado.prog2.entities.Pedido;
import integrado.prog2.exception.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rocio Aguero
 * Comision: 6
 * Participante 3
 */
public class PedidoRepository implements IRepository<Pedido>{
    
    // Nuestra "Base de Datos" en memoria
    private static final List<Pedido> tablaPedidos = new ArrayList();
    private static Long ultimoId = 0L;
    
    @Override
    public void guardar(Pedido pedido) {
        if (pedido.getId() == null) {
            ultimoId++;
            pedido.setId(ultimoId);
            tablaPedidos.add(pedido);
        } else {
            Pedido existente = buscarPorId(pedido.getId());
            if (existente != null) {
                existente.setFecha(pedido.getFecha());
                existente.setEstado(pedido.getEstado());
                existente.setTotal(pedido.getTotal());
                existente.setFormaPago(pedido.getFormaPago());
                existente.setUsuario(pedido.getUsuario());
                existente.setDetalles(pedido.getDetalles());
            }
        }
    }
    
    @Override
    public Pedido buscarPorId(Long id) {
        for (Pedido pedido : tablaPedidos) {
            if (pedido.getId().equals(id) && !pedido.isEliminado()) {
                return pedido;
            }
        }
        return null;
    }
    
    @Override
    public List<Pedido> listarTodos(){
        List<Pedido> activos = new ArrayList<>();
        for(Pedido pedido : tablaPedidos){
            if(!pedido.isEliminado()){
                activos.add(pedido);
            }
        }
        return activos;
    }
    
    @Override
    public void eliminar(Long id){
        Pedido pedido = buscarPorId(id);
        if(pedido == null){
            throw new EntidadNoEncontradaException("Error al eliminar)");
        }
        pedido.setEliminado(true);
    }
}
