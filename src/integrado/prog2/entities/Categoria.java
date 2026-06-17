
package integrado.prog2.entities;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public class Categoria extends Base {
    
    private String nombre;

    // Constructor vacío obligatorio
    public Categoria() {
        super();
    }

    // Constructor parametrizado para facilitar la creación
    public Categoria(String nombre) {
        super();
        this.nombre = nombre;
    }

    // Getter y Setter
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Sobrecarga de toString para poder listarla lindo por consola más adelante
    @Override
    public String toString() {
        return String.format("Categoría [ID: %d | Nombre: %s | Creada: %s | Eliminada: %s]", 
                getId(), nombre, getCreatedAt(), isEliminado() ? "Sí" : "No");
    }
}
