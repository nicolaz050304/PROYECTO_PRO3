package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.message.bl.MensajeBL;
import pe.edu.pe.pucp.proyecto.message.implbl.MensajeBLImpl;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.notif.bl.NotificacionBL;
import pe.edu.pe.pucp.proyecto.notif.implbl.NotificacionBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.MensajeDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.MensajeMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Mensajería (RF17). Solo expone DTOs planos (nunca la entidad Mensaje,
 * cuyo emisor/reserva tienen ciclos) y delega en la BL existente (MensajeBL).
 *
 * El chat se organiza POR RESERVA: cliente y anfitrión conversan sobre una reserva, así que
 * el hilo se identifica por el id de la reserva.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/MensajeRS
 */
@Path("MensajeRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MensajeRS {

    private final MensajeBL mensajeBL = new MensajeBLImpl();
    private final ReservaBL reservaBL = new ReservaBLImpl();
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final NotificacionBL notificacionBL = new NotificacionBLImpl();

    /**
     * GET MensajeRS/reserva/{idReserva} -> mensajes del chat de esa reserva (orden cronológico).
     */
    @GET
    @Path("reserva/{idReserva}")
    public List<MensajeDTO> listarPorReserva(@PathParam("idReserva") int idReserva) {
        List<MensajeDTO> salida = new ArrayList<>();
        List<Mensaje> lista = mensajeBL.listarPorChat(idReserva);
        if (lista != null) {
            for (Mensaje m : lista) {
                salida.add(MensajeMapper.toDTO(m));
            }
        }
        return salida;
    }

    /**
     * POST MensajeRS -> envía un mensaje al chat de una reserva.
     * La BL valida (no vacío, máx 500 chars, emisor y reserva no nulos) antes de persistir.
     * 201 con el DTO creado (incluye su id); 400 con el mensaje de error si la validación falla.
     */
    @POST
    public Response enviar(MensajeDTO dto) {
        try {
            Mensaje m = MensajeMapper.toEntity(dto);
            int id = mensajeBL.insertar(m);   // valida no vacío / máx 500 / emisor+reserva
            m.setIdMensaje(id);

            // EVENTO 3 (RF18): notificamos al OTRO participante del chat (el que NO envió el mensaje).
            // Los participantes son el huésped (de la reserva) y el anfitrión (dueño del alojamiento).
            // La reserva trae el alojamiento como proxy solo-id, así que cargamos el alojamiento por su id
            // para obtener el id del dueño. Va en try/catch: la notificación NUNCA debe romper el envío.
            try {
                Reserva r = reservaBL.obtenerPorId(dto.getIdReserva());
                if (r != null) {
                    int idHuesped = r.getInvitado() != null ? r.getInvitado().getIdUsuario() : 0;
                    int idAnfitrion = 0;
                    if (r.getAlojamiento() != null) {
                        Alojamiento alo = alojamientoBL.obtenerPorId(r.getAlojamiento().getIdAlojamiento());
                        if (alo != null && alo.getDuenho() != null) {
                            idAnfitrion = alo.getDuenho().getIdUsuario();
                        }
                    }
                    // El destinatario es el participante que NO es el emisor.
                    int idDestino = (dto.getIdEmisor() == idHuesped) ? idAnfitrion : idHuesped;
                    if (idDestino > 0) {
                        // En HILO de segundo plano: no bloqueamos el envío del mensaje esperando el
                        // INSERT de la notificación (el receptor la verá en su feed igual).
                        notificacionBL.crearAsync("Nuevo mensaje",
                                "Tienes un nuevo mensaje en una de tus reservas.", idDestino, "MENSAJE");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return Response.status(Response.Status.CREATED)
                           .entity(MensajeMapper.toDTO(m)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * PUT MensajeRS/reserva/{idReserva}/leidos/{idUsuario} -> marca como leídos los mensajes
     * de esa reserva dirigidos al usuario (los que le enviaron, no los suyos). Se invoca cuando
     * el usuario ABRE el chat.
     */
    @PUT
    @Path("reserva/{idReserva}/leidos/{idUsuario}")
    public Response marcarLeidos(@PathParam("idReserva") int idReserva,
                                 @PathParam("idUsuario") int idUsuario) {
        try {
            mensajeBL.marcarLeidos(idReserva, idUsuario);
            return Response.ok("{\"ok\":true}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET MensajeRS/reserva/{idReserva}/noleidos/{idUsuario} -> nº de mensajes no leídos que tiene
     * el usuario en esa reserva (mensajes que le enviaron y aún no abrió). Alimenta el badge del frontend.
     */
    @GET
    @Path("reserva/{idReserva}/noleidos/{idUsuario}")
    public Response contarNoLeidos(@PathParam("idReserva") int idReserva,
                                   @PathParam("idUsuario") int idUsuario) {
        int n = 0;
        try {
            n = mensajeBL.contarNoLeidos(idReserva, idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok("{\"noLeidos\":" + n + "}").build();
    }
}
