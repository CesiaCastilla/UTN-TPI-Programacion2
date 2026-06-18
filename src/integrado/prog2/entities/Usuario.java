
package integrado.prog2.entities;

import integrado.prog2.enums.Rol;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class Usuario extends Base {

    private String nombre;
    private String apellido;
    private String mail;
    private String celular;
    private String contrasena;
    private Rol rol;

    // Constructor vacío obligatorio
    public Usuario() {
        super();
    }

    // Constructor parametrizado para facilitar la creación
    public Usuario(String nombre, String apellido, String mail, String celular, String contrasena, Rol rol) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.celular = celular;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    // Sobrecarga de toString para listar por consola sin exponer la contraseña
    @Override
    public String toString() {
        return String.format("Usuario [ID: %d | Nombre: %s %s | Mail: %s | Rol: %s | Eliminado: %s]",
                getId(), nombre, apellido, mail, rol, isEliminado() ? "Sí" : "No");
    }
}
