package pe.edu.pe.pucp.proyecto.notif.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.notif.Notificaciones;

import java.util.List;

public interface NotificacionIDAO extends IDAO <Notificaciones,Integer> {
    List<Notificaciones> listAll();
}
