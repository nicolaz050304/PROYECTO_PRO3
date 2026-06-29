package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;
import pe.edu.pe.pucp.proyecto.bloqueo.bl.BloqueoFechaBL;
import pe.edu.pe.pucp.proyecto.bloqueo.implbl.BloqueoFechaBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.BloqueoFechaDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Recurso REST de bloqueos de fecha del anfitrión (RF30): el anfitrión marca rangos
 * en los que su alojamiento no admite reservas. Estos rangos también se devuelven como
 * "ocupadas" en {@link ReservaRS}, así el calendario del huésped los bloquea.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/BloqueoFechaRS
 */
@Path("BloqueoFechaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BloqueoFechaRS {

    private final BloqueoFechaBL bloqueoBL = new BloqueoFechaBLImpl();

    /** GET BloqueoFechaRS/alojamiento/{id} -> bloqueos de un alojamiento. */
    @GET
    @Path("alojamiento/{id}")
    public List<BloqueoFechaDTO> listar(@PathParam("id") int idAlojamiento) {
        List<BloqueoFechaDTO> salida = new ArrayList<>();
        for (BloqueoFecha b : bloqueoBL.listarPorAlojamiento(idAlojamiento)) {
            salida.add(toDTO(b));
        }
        return salida;
    }

    /** POST BloqueoFechaRS -> crea un bloqueo. Body { idAlojamiento, fechaInicio, fechaFin, motivo }. */
    @POST
    public Response agregar(BloqueoFechaDTO dto) {
        if (dto == null || dto.getIdAlojamiento() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"idAlojamiento es obligatorio\"}").build();
        }
        Date inicio = parsearFechaIso(dto.getFechaInicio());
        Date fin = parsearFechaIso(dto.getFechaFin());
        if (inicio == null || fin == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"fechaInicio y fechaFin son obligatorias (yyyy-MM-dd)\"}").build();
        }
        try {
            BloqueoFecha b = new BloqueoFecha(0, dto.getIdAlojamiento(), inicio, fin, dto.getMotivo());
            int id = bloqueoBL.agregar(b);
            b.setIdBloqueo(id);
            return Response.status(Response.Status.CREATED).entity(toDTO(b)).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    /** DELETE BloqueoFechaRS/{id} -> elimina un bloqueo. */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        bloqueoBL.eliminar(id);
        return Response.noContent().build();
    }

    private static BloqueoFechaDTO toDTO(BloqueoFecha b) {
        return new BloqueoFechaDTO(
                b.getIdBloqueo(),
                b.getIdAlojamiento(),
                formatearFechaIso(b.getFechaInicio()),
                formatearFechaIso(b.getFechaFin()),
                b.getMotivo());
    }

    /** Formatea a ISO "yyyy-MM-dd"; null -> null. */
    private static String formatearFechaIso(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(fecha);
    }

    /** Parsea ISO "yyyy-MM-dd" a Date (no lenient); vacío/null/inválido -> null. */
    private static Date parsearFechaIso(String iso) {
        if (iso == null || iso.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(iso.trim());
        } catch (ParseException ex) {
            return null;
        }
    }
}
