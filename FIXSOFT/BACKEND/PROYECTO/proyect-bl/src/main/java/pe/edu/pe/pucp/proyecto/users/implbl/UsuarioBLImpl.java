package pe.edu.pe.pucp.proyecto.users.implbl;


import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.dao.UsuarioIDAO;
import pe.edu.pe.pucp.proyecto.users.impl.UsuarioImpl;

import java.util.List;

public class UsuarioBLImpl implements UsuarioBL {

    private UsuarioIDAO daoUsuario = new UsuarioImpl();

    @Override
    public int insertar(Usuario usuario) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES ---

        // 1. Validar que el correo tenga un formato mínimo
        if (usuario.getCorreo() == null || !usuario.getCorreo().contains("@")) {
            throw new RuntimeException("Error: El correo electrónico no tiene un formato válido.");
        }

        // 2. Validar longitud del documento (Si es DNI, debe tener 8 dígitos)
        // Usamos la comparación directa y segura con el Enum
        if (usuario.getTipoDocumento() == TipoDocumento.DNI &&
                (usuario.getNumeroDocumento() == null || usuario.getNumeroDocumento().length() != 8)) {
            throw new RuntimeException("Error: El DNI debe tener exactamente 8 dígitos.");
        }

        // Si pasa las reglas, llamamos al DAO
        return daoUsuario.save(usuario).getIdUsuario();
    }

    @Override
    public List<Usuario> listarTodos() {
        return daoUsuario.listAll();
    }

    @Override
    public Usuario obtenerPorId(Integer id) {
        return daoUsuario.load(id);
    }

    @Override
    public int modificar(Usuario usuario) {
        // Al modificar, también aseguramos que no borren el correo
        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
            throw new RuntimeException("Error: El correo es obligatorio para la actualización.");
        }
        return daoUsuario.update(usuario).getIdUsuario();
    }

    @Override
    public int eliminar(Usuario usuario) {
        // Aquí podrías aplicar una "eliminación lógica" (cambiar estado a SUSPENDIDO)
        // en lugar de borrarlo físicamente de la BD
        daoUsuario.remove(usuario);
        return 1;
    }
}