
package integrado.prog2.service;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.repository.UsuarioRepository;
import java.util.List;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crear(String nombre, String apellido, String mail, String celular, String contrasena, Rol rol) {
        validarMail(mail, null);

        Usuario usuario = new Usuario(nombre, apellido, mail, celular, contrasena, rol);
        usuarioRepository.guardar(usuario);
        return usuario;
    }

    public Usuario editar(Long id, String nombre, String apellido, String mail, String celular, String contrasena, Rol rol) {
        Usuario existente = obtenerPorId(id);
        validarMail(mail, id);

        existente.setNombre(nombre);
        existente.setApellido(apellido);
        existente.setMail(mail);
        existente.setCelular(celular);
        existente.setContrasena(contrasena);
        existente.setRol(rol);

        usuarioRepository.guardar(existente);
        return existente;
    }

    public void eliminar(Long id) {
        usuarioRepository.eliminar(id);
    }

    public List<Usuario> listar() {
        return usuarioRepository.listarTodos();
    }

    public Usuario obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id);
        if (usuario == null) {
            throw new EntidadNoEncontradaException("No existe un usuario activo con ID " + id);
        }
        return usuario;
    }

    private void validarMail(String mail, Long idAEditar) {
        if (mail == null || mail.isBlank()) {
            throw new ValidationException("El mail no puede estar vacío");
        }
        for (Usuario u : usuarioRepository.listarTodos()) {
            boolean esElMismoUsuario = idAEditar != null && u.getId().equals(idAEditar);
            if (!esElMismoUsuario && u.getMail().equalsIgnoreCase(mail)) {
                throw new ValidationException("Ya existe un usuario registrado con el mail " + mail);
            }
        }
    }
}
