
package integrado.prog2.entities;

/**
 *
 * @author Rocio Aguero
 * Comision: 6
 * Participante 3
 */
public class DetallePedido extends Base {
    
    private int cantidad;
    private Double subTotal;
    private Producto producto;
    
    // Constructor vacío obligatorio
    public DetallePedido(){
        super();
    }
    
    // Constructor parametrizado para facilitar la creación
    public DetallePedido(int cantidad, Double subTotal, Producto producto) {
        super();
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.producto = producto;
    }
    
    // Getters y Setters
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetallePedido{" + "cantidad=" + cantidad + ", subTotal= $" + subTotal + ", producto=" + producto.getNombre() + '}';
    }
 
    
}
