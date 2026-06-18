
package integrado.prog2.repository;

import integrado.prog2.entities.Usuario;
import integrado.prog2.exception.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cesia Castilla
 * Comision: 5
 * Participante 2
 */

public class UsuarioRepository implements IRepository<Usuario> {

    // Nuestra "Base de Datos" en memoria
    private static final List<Usuario> tablaUsuarios = new ArrayList<>();
    private static Long ultimoId = 0L;

    @Override
    public void guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            // Es un nuevo usuario (Insert)
            ultimoId++;
            usuario.setId(ultimoId);
            tablaUsuarios.add(usuario);
        } else {
            // Es una actualización (Update)
            Usuario existente = buscarPorId(usuario.getId());
            if (existente != null) {
                existente.setNombre(usuario.getNombre());
                existente.setApellido(usuario.getApellido());
                existente.setMail(usuario.getMail());
                existente.setCelular(usuario.getCelular());
                existente.setContrasena(usuario.getContrasena());
                existente.setRol(usuario.getRol());
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> activos = new ArrayList<>();
        for (Usuario u : tablaUsuarios) {
            if (!u.isEliminado()) { // Solo mostramos los que no tienen baja lógica
                activos.add(u);
            }
        }
        return activos;
    }

    @Override
    public Usuario buscarPorId(Long id) {
        for (Usuario u : tablaUsuarios) {
            if (u.getId().equals(id) && !u.isEliminado()) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);

        if (usuario == null) {
            throw new EntidadNoEncontradaException("Error al eliminar: No existe el usuario con ID " + id);
        }

        // Baja lógica: los pedidos existentes del usuario siguen siendo consultables
        usuario.setEliminado(true);
    }
}
