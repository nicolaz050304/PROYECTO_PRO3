package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.notif.Notificaciones;
import pe.edu.pe.pucp.proyecto.web.dto.NotificacionDTO;

/**
 * Mapper de Notificaciones (entidad) a su DTO plano. El usuario viene como proxy solo-id
 * desde el DAO, por eso solo exponemos su id (idUsuario), no el objeto completo.
 */
public final class NotificacionMapper {

    private NotificacionMapper() {}

    public static NotificacionDTO toDTO(Notificaciones n) {
        if (n == null) return null;
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(n.getIdNotificacion());
        dto.setTitulo(n.getTitulo());
        dto.setMensaje(n.getMensaje());
        dto.setLeido(n.isLeido());
        dto.setIdUsuario(n.getUsuario() != null ? n.getUsuario().getIdUsuario() : 0);
        return dto;
    }
}
