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

import pe.edu.pe.pucp.proyecto.incidencia.Incidencia;
import pe.edu.pe.pucp.proyecto.incidencia.bl.IncidenciaBL;
import pe.edu.pe.pucp.proyecto.incidencia.implbl.IncidenciaBLImpl;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.IncidenciaDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de incidencias / soporte (RF28): cualquier usuario abre un ticket (asunto + descripción
 * + prioridad) y el admin lo gestiona cambiando su estado. El DAO guarda solo el id del usuario;
 * aquí se resuelve su nombre/correo con UsuarioBL (cacheado por request), igual que DenunciaRS.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/IncidenciaRS
 */
@Path("IncidenciaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidenciaRS {

    private final IncidenciaBL incidenciaBL = new IncidenciaBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET IncidenciaRS -> todas las incidencias (para el panel del admin). */
    @GET
    public List<IncidenciaDTO> listar() {
        Map<Integer, Usuario> cache = new HashMap<>();
        List<IncidenciaDTO> salida = new ArrayList<>();
        for (Incidencia i : incidenciaBL.listarTodas()) {
            salida.add(toDTO(i, cache));
        }
        return salida;
    }

    /**
     * POST IncidenciaRS -> un usuario abre un ticket.
     * Body { usuarioId, asunto, descripcion, prioridad }. Estado y fecha los asigna el backend.
     */
    @POST
    public Response crear(IncidenciaDTO dto) {
        if (dto == null || dto.getUsuarioId() <= 0
                || dto.getAsunto() == null || dto.getAsunto().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"usuarioId y asunto son obligatorios\"}").build();
        }
        try {
            Incidencia inc = new Incidencia(0, dto.getUsuarioId(), dto.getAsunto().trim(),
                    dto.getDescripcion(), dto.getPrioridad(), Incidencia.ABIERTO, new Date());
            int id = incidenciaBL.agregar(inc);
            inc.setIdIncidencia(id);
            return Response.status(Response.Status.CREATED).entity(toDTO(inc, new HashMap<>())).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    /** PUT IncidenciaRS/{id}/estado -> el admin cambia el estado. Body { estado }. */
    @PUT
    @Path("{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, IncidenciaDTO dto) {
        if (dto == null || dto.getEstado() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"estado es obligatorio\"}").build();
        }
        try {
            incidenciaBL.cambiarEstado(id, dto.getEstado());
            return Response.noContent().build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    // ------------------------------------------------------------------

    private IncidenciaDTO toDTO(Incidencia i, Map<Integer, Usuario> cache) {
        IncidenciaDTO dto = new IncidenciaDTO();
        dto.setId(i.getIdIncidencia());
        dto.setUsuarioId(i.getIdUsuario());
        Usuario u = resolverUsuario(i.getIdUsuario(), cache);
        if (u != null) {
            String n = u.getNombre() != null ? u.getNombre() : "";
            String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
            dto.setUsuarioNombre((n + " " + ap).trim());
            dto.setUsuarioCorreo(u.getCorreo());
        } else {
            dto.setUsuarioNombre("");
            dto.setUsuarioCorreo("");
        }
        dto.setAsunto(i.getAsunto());
        dto.setDescripcion(i.getDescripcion());
        dto.setPrioridad(i.getPrioridad());
        dto.setEstado(i.getEstado());
        dto.setFecha(formatearFechaIso(i.getFecha()));
        return dto;
    }

    private Usuario resolverUsuario(int idUsuario, Map<Integer, Usuario> cache) {
        if (idUsuario <= 0) return null;
        if (cache.containsKey(idUsuario)) return cache.get(idUsuario);
        Usuario u;
        try {
            u = usuarioBL.obtenerPorId(idUsuario);
        } catch (RuntimeException ex) {
            u = null;
        }
        cache.put(idUsuario, u);
        return u;
    }

    /** Formatea a ISO "yyyy-MM-dd"; null -> null. */
    private static String formatearFechaIso(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(fecha);
    }
}
