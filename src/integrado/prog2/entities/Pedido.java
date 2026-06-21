
package integrado.prog2.entities;

import integrado.prog2.enums.EstadoPedido;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.interfaces.Calculable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rocio Aguero
 * Comision: 6
 * Participante 3
 */
public class Pedido extends Base implements Calculable  {
    private LocalDate fecha;
    private EstadoPedido estado;
    private Double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private List<DetallePedido> detalles;
    
     // Constructor vacío obligatorio
    public Pedido() {
        super();
        this.total = 0.0;
        this.detalles = new ArrayList<>();
        
    }
    
    // Constructor parametrizado para facilitar la creación
    public Pedido(LocalDate fecha, EstadoPedido estado, double total, FormaPago formaPago, Usuario usuario, List<DetallePedido> detalles) {
        super();
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.formaPago = formaPago;
        this.usuario = usuario;
        this.detalles = detalles != null ? detalles : new ArrayList<>();
    }
    
    //Getters y setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Pedido{" + "fecha=" + fecha + ", estado=" + estado + ", total=" + total + ", formaPago=" + formaPago + ", usuario=" + usuario + ", detalles=" + detalles + '}';
    }
    

    @Override
    public void calcularTotal() {
        this.total = detalles.stream().mapToDouble(d -> d.getSubTotal() != null ? d.getSubTotal() : 0.0).sum();
      
    }
    
    //Metodo de Agregar un detalle
    public void addDetallePedido(int cantidad, Double precio,Producto producto){
        if(cantidad <= 0){
            throw new ValidationException("La cantidad debe ser mayor a cero");
        }
        Double calculaSubTotal = cantidad * precio;
        DetallePedido detalle = new DetallePedido(cantidad,calculaSubTotal,producto);
        detalles.add(detalle);
        calcularTotal();
    }
    
    //Metodo de buscar 
    public DetallePedido findeDetallePedidoByProducto(Producto producto){
        for(DetallePedido detalle : detalles){
            if(detalle.getProducto() != null && detalle.getProducto().getId().equals(producto.getId())){
                return detalle;
            }
                    
        }
        return null;
    }
    
    //Metodo eliminar
    public void deleteDetallePedidoByProducto(Producto producto){
        DetallePedido detalle = findeDetallePedidoByProducto(producto);
        if(detalle != null){
            detalles.remove(detalle);
            calcularTotal();
        }
    }
}
