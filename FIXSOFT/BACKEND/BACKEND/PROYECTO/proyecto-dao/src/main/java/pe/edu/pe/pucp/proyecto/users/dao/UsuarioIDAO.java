package pe.edu.pe.pucp.proyecto.users.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.users.Usuario;

import java.util.List;

public interface UsuarioIDAO extends IDAO <Usuario,Integer>{
    List<Usuario> listAll();

    // Búsqueda por correo (necesaria para login). Devuelve null si no existe.
    Usuario buscarPorCorreo(String correo);
}
