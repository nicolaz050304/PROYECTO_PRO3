package pe.edu.pe.pucp.proyecto.notif.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.notif.Notificaciones;

import java.util.List;

public interface NotificacionIDAO extends IDAO <Notificaciones,Integer> {
    List<Notificaciones> listAll();

    // Notificaciones de un usuario, más recientes primero.
    List<Notificaciones> listarPorUsuario(int idUsuario);
    // Marca una notificación como leída (al abrirla / verla).
    void marcarLeida(int idNotificacion);
    // Nº de notificaciones no leídas del usuario (alimenta el badge).
    int contarNoLeidas(int idUsuario);
}
