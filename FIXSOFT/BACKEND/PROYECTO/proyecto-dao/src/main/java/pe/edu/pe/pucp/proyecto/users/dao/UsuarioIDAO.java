package pe.edu.pe.pucp.proyecto.users.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.users.Usuario;

import java.util.List;

public interface UsuarioIDAO extends IDAO <Usuario,Integer>{
    List<Usuario> listAll();
}
