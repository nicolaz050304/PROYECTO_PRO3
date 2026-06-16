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

    // Login plano: busca por correo y compara password tal cual (el hashing vendrá después).
    // Devuelve el usuario si las credenciales coinciden; null si el correo no existe o no coincide.
    Usuario autenticar(String correo, String password);
}