package pe.edu.pe.pucp.proyecto.notif.implbl;

import pe.edu.pe.pucp.proyecto.notif.Notificaciones; // Importamos tu clase en plural
import pe.edu.pe.pucp.proyecto.notif.bl.NotificacionBL;
import pe.edu.pe.pucp.proyecto.notif.dao.NotificacionIDAO;
import pe.edu.pe.pucp.proyecto.notif.impl.NotificacionImpl;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.users.Usuario;

import java.util.List;

public class NotificacionBLImpl implements NotificacionBL {

    private NotificacionIDAO daoNotificacion = new NotificacionImpl();

    @Override
    public int insertar(Notificaciones notificacion) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES DE NOTIFICACIONES ---

        // 1. Validar que tenga un título
        if (notificacion.getTitulo() == null || notificacion.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("Error: La notificación debe tener un título.");
        }

        // 2. CORREGIDO: Validar que tenga un mensaje (usamos getMensaje en lugar de getContenido)
        if (notificacion.getMensaje() == null || notificacion.getMensaje().trim().isEmpty()) {
            throw new RuntimeException("Error: La notificación debe tener un contenido o mensaje.");
        }

        // 3. Validar que esté asociada a un usuario destino
        if (notificacion.getUsuario() == null || notificacion.getUsuario().getIdUsuario() <= 0) {
            throw new RuntimeException("Error: La notificación debe estar asignada a un usuario válido.");
        }

        return daoNotificacion.save(notificacion).getIdNotificacion();
    }

    @Override
    public List<Notificaciones> listarTodos() {
        return daoNotificacion.listAll();
    }

    @Override
    public Notificaciones obtenerPorId(Integer id) {
        return daoNotificacion.load(id);
    }

    @Override
    public int modificar(Notificaciones notificacion) {
        if (notificacion.getTitulo() == null || notificacion.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("Error: El título de la notificación no puede estar vacío al actualizar.");
        }
        return daoNotificacion.update(notificacion).getIdNotificacion();
    }

    @Override
    public int eliminar(Notificaciones notificacion) {
        daoNotificacion.remove(notificacion);
        return 1;
    }

    // Notificaciones de un usuario (recientes primero): delega al DAO.
    @Override
    public List<Notificaciones> listarPorUsuario(int idUsuario) {
        return daoNotificacion.listarPorUsuario(idUsuario);
    }

    @Override
    public void marcarLeida(int idNotificacion) {
        daoNotificacion.marcarLeida(idNotificacion);
    }

    @Override
    public int contarNoLeidas(int idUsuario) {
        return daoNotificacion.contarNoLeidas(idUsuario);
    }

    // Atajo de creación (lo usará la generación automática en Fase 2). Reutiliza insertar (que valida).
    // El usuario destino se referencia como proxy solo-id (mismo patrón que el DAO con Invitado).
    @Override
    public int crear(String titulo, String mensaje, int idUsuario) {
        Notificaciones n = new Notificaciones();
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setLeido(false);   // nace no leída
        Usuario u = new Invitado();
        u.setIdUsuario(idUsuario);
        n.setUsuario(u);
        return insertar(n);
    }
}