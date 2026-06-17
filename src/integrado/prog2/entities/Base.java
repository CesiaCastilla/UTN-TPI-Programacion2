
package integrado.prog2.entities;

import java.time.LocalDateTime;

/**
 *
 * @author Alfredo Castillo
 * Comision: 3
 * Participante 1
 */

public abstract class Base {
    
    private Long id;
    private boolean eliminado;
    private LocalDateTime createdAt;

    // Constructor por defecto que inicializa la fecha de creación automáticamente
    public Base() {
        this.createdAt = LocalDateTime.now();
        this.eliminado = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
