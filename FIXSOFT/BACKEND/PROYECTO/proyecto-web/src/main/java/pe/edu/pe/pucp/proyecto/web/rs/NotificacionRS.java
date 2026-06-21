package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.notif.Notificaciones;
import pe.edu.pe.pucp.proyecto.notif.bl.NotificacionBL;
import pe.edu.pe.pucp.proyecto.notif.implbl.NotificacionBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.NotificacionDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.NotificacionMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Notificaciones (RF18, Fase 1). Solo expone DTOs planos (nunca la entidad,
 * cuyo Usuario tiene ciclos) y delega en la BL existente (NotificacionBL).
 *
 * Las notificaciones son POR USUARIO: cada usuario consulta y marca como leídas las suyas.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/NotificacionRS
 */
@Path("NotificacionRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificacionRS {

    private final NotificacionBL notificacionBL = new NotificacionBLImpl();

    /**
     * GET NotificacionRS/usuario/{idUsuario} -> notificaciones del usuario (recientes primero).
     */
    @GET
    @Path("usuario/{idUsuario}")
    public List<NotificacionDTO> listarPorUsuario(@PathParam("idUsuario") int idUsuario) {
        List<NotificacionDTO> salida = new ArrayList<>();
        List<Notificaciones> lista = notificacionBL.listarPorUsuario(idUsuario);
        if (lista != null) {
            for (Notificaciones n : lista) {
                salida.add(NotificacionMapper.toDTO(n));
            }
        }
        return salida;
    }

    /**
     * GET NotificacionRS/usuario/{idUsuario}/noleidas -> nº de notificaciones no leídas (para el badge).
     */
    @GET
    @Path("usuario/{idUsuario}/noleidas")
    public Response contarNoLeidas(@PathParam("idUsuario") int idUsuario) {
        int n = 0;
        try {
            n = notificacionBL.contarNoLeidas(idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok("{\"noLeidas\":" + n + "}").build();
    }

    /**
     * PUT NotificacionRS/{idNotificacion}/leida -> marca una notificación como leída.
     */
    @PUT
    @Path("{idNotificacion}/leida")
    public Response marcarLeida(@PathParam("idNotificacion") int idNotificacion) {
        try {
            notificacionBL.marcarLeida(idNotificacion);
            return Response.ok("{\"ok\":true}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
