package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.messages.Mensaje;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.web.dto.MensajeDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mapper entre la entidad Mensaje y su DTO plano.
 *
 * En toDTO el emisor puede venir como proxy solo-id (el DAO lo arma solo con el id), por eso
 * nombreEmisor sale null en ese caso: está bien, el frontend lo resuelve por idEmisor.
 * En toEntity se reconstruyen el emisor y la reserva SOLO con su id (proxies), que es lo único
 * que la BL/DAO necesita para persistir (emisor_id, id_reserva).
 */
public final class MensajeMapper {

    private MensajeMapper() {}

    private static final String FORMATO_FECHA = "yyyy-MM-dd HH:mm";

    public static MensajeDTO toDTO(Mensaje m) {
        if (m == null) return null;
        MensajeDTO dto = new MensajeDTO();
        dto.setIdMensaje(m.getIdMensaje());
        dto.setTexto(m.getTexto());
        dto.setFechaEnvio(m.getFechaEnvio() != null
                ? new SimpleDateFormat(FORMATO_FECHA).format(m.getFechaEnvio())
                : null);
        dto.setIdEmisor(m.getEmisor() != null ? m.getEmisor().getIdUsuario() : 0);
        // nombreEmisor puede ser null si el emisor es proxy solo-id (lo resuelve el frontend).
        dto.setNombreEmisor(m.getEmisor() != null ? m.getEmisor().getNombre() : null);
        dto.setIdReserva(m.getReserva() != null ? m.getReserva().getIdReserva() : 0);
        dto.setLeido(m.isLeido());
        return dto;
    }

    public static Mensaje toEntity(MensajeDTO dto) {
        Mensaje m = new Mensaje();
        m.setTexto(dto.getTexto());
        // Si no viene fecha desde el cliente, la fijamos ahora (momento del envío).
        m.setFechaEnvio(new Date());

        // Emisor como proxy solo-id: misma convención que usa el DAO (new Invitado() + setIdUsuario).
        Usuario emisor = new Invitado();
        emisor.setIdUsuario(dto.getIdEmisor());
        m.setEmisor(emisor);

        // Reserva como proxy solo-id: el chat pertenece a esa reserva.
        Reserva reserva = new Reserva();
        reserva.setIdReserva(dto.getIdReserva());
        m.setReserva(reserva);

        return m;
    }
}
