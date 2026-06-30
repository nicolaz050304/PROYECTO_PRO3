package pe.edu.pe.pucp.proyecto.notif.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.notif.Notificaciones; // Verifica que este import coincida con tu modelo

import java.util.List;

public interface NotificacionBL extends IBL<Notificaciones, Integer> {
    // Notificaciones de un usuario (recientes primero).
    List<Notificaciones> listarPorUsuario(int idUsuario);
    // Marca una notificación como leída.
    void marcarLeida(int idNotificacion);
    // Nº de no leídas del usuario (badge).
    int contarNoLeidas(int idUsuario);
    // Atajo para crear una notificación a un usuario (categoría GENERAL por defecto).
    int crear(String titulo, String mensaje, int idUsuario);
    // Igual pero con categoría explícita (RESERVA / MENSAJE / PAGO / GENERAL) para filtrar el feed.
    int crear(String titulo, String mensaje, int idUsuario, String categoria);
    // Crea la notificación en un HILO en segundo plano (no bloquea la petición REST que la dispara).
    void crearAsync(String titulo, String mensaje, int idUsuario, String categoria);
}