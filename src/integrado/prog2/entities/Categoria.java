
package integrado.prog2.entities;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class Categoria extends Base {
    
    private String nombre;
    private String descripcion;

    // Constructor vacío obligatorio
    public Categoria() {
        super();
    }

    // Constructor parametrizado actualizado para recibir ambos datos
    public Categoria(String nombre, String descripcion) {
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // --- GETTERS Y SETTERS ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // <--- METODOS NUEVOS AGREGADOS --->
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Sobrecarga de toString optimizada para mostrar la descripción
    @Override
    public String toString() {
        return String.format("Categoría [ID: %d | Nombre: %s | Descripción: %s | Creada: %s | Eliminada: %s]", 
                getId(), nombre, descripcion, getCreatedAt(), isEliminado() ? "Sí" : "No");
    }
}
