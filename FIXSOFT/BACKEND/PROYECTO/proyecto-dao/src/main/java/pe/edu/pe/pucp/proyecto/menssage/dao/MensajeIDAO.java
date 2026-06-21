package pe.edu.pe.pucp.proyecto.menssage.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import java.util.List;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;

public interface MensajeIDAO extends IDAO<Mensaje, Integer> {
    List<Mensaje> listarPorChat(int idReserva);
    List<Mensaje> listAll();

    // Marca como leídos los mensajes de esa reserva que NO envió el lector (los que le enviaron a él).
    void marcarLeidos(int idReserva, int idUsuarioLector);

    // Cuántos mensajes no leídos tiene el usuario en esa reserva (los que le enviaron y no abrió).
    int contarNoLeidos(int idReserva, int idUsuario);
}
