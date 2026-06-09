package pe.edu.pe.pucp.proyecto.users.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.users.Usuario;

import java.util.List;

public interface UsuarioBL extends IBL<Usuario, Integer> {
    int insertar(Usuario usuario);

    List<Usuario> listarTodos();

    Usuario obtenerPorId(Integer id);

    int modificar(Usuario usuario);

    int eliminar(Usuario usuario);
    // Aquí puedes definir métodos como autenticar o validarCorreo
}