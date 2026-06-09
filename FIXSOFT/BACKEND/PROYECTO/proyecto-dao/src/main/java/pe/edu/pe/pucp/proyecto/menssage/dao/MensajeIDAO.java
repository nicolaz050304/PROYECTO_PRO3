package pe.edu.pe.pucp.proyecto.menssage.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import java.util.List;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;

public interface MensajeIDAO extends IDAO<Mensaje, Integer> {
    List<Mensaje> listarPorChat(int idReserva);
    List<Mensaje> listAll();
}
