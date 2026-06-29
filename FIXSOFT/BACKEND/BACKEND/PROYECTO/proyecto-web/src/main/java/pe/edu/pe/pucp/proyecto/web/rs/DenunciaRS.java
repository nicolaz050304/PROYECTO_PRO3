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

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.denuncia.Denuncia;
import pe.edu.pe.pucp.proyecto.denuncia.bl.DenunciaBL;
import pe.edu.pe.pucp.proyecto.denuncia.implbl.DenunciaBLImpl;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.DenunciaDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de denuncias (RF31): el huésped reporta un alojamiento y el admin gestiona
 * el estado. El DAO solo guarda ids; aquí se resuelven los nombres (denunciante, alojamiento,
 * anfitrión) con AlojamientoBL/UsuarioBL, cacheados por request (anti N+1), igual que ReservaRS.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/DenunciaRS
 */
@Path("DenunciaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DenunciaRS {

    private final DenunciaBL denunciaBL = new DenunciaBLImpl();
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET DenunciaRS -> todas las denuncias (para el panel del admin). */
    @GET
    public List<DenunciaDTO> listar() {
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<DenunciaDTO> salida = new ArrayList<>();
        for (Denuncia d : denunciaBL.listarTodas()) {
            salida.add(toDTO(d, aloCache, usuarioCache));
        }
        return salida;
    }

    /**
     * POST DenunciaRS -> el huésped crea una denuncia.
     * Body { denuncianteId, alojamientoId, motivo, descripcion }. El anfitrión se deriva
     * del alojamiento; estado y fecha los asigna el backend.
     */
    @POST
    public Response crear(DenunciaDTO dto) {
        if (dto == null || dto.getDenuncianteId() <= 0 || dto.getAlojamientoId() <= 0
                || dto.getMotivo() == null || dto.getMotivo().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"denuncianteId, alojamientoId y motivo son obligatorios\"}").build();
        }
        try {
            // Derivamos el anfitrión del alojamiento (el "usuario denunciado").
            int anfitrionId = 0;
            Alojamiento alo = alojamientoBL.obtenerPorId(dto.getAlojamientoId());
            if (alo != null && alo.getDuenho() != null) {
                anfitrionId = alo.getDuenho().getIdUsuario();
            }

            Denuncia d = new Denuncia(0, dto.getDenuncianteId(), dto.getAlojamientoId(), anfitrionId,
                    dto.getMotivo().trim(), dto.getDescripcion(), Denuncia.EN_REVISION, new Date());
            int id = denunciaBL.agregar(d);
            d.setIdDenuncia(id);

            return Response.status(Response.Status.CREATED)
                    .entity(toDTO(d, new HashMap<>(), new HashMap<>())).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    /** PUT DenunciaRS/{id}/estado -> el admin cambia el estado. Body { estado }. */
    @PUT
    @Path("{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, DenunciaDTO dto) {
        if (dto == null || dto.getEstado() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"estado es obligatorio\"}").build();
        }
        try {
            denunciaBL.cambiarEstado(id, dto.getEstado());
            return Response.noContent().build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    // ------------------------------------------------------------------

    private DenunciaDTO toDTO(Denuncia d, Map<Integer, Alojamiento> aloCache,
                              Map<Integer, String> usuarioCache) {
        DenunciaDTO dto = new DenunciaDTO();
        dto.setId(d.getIdDenuncia());
        dto.setDenuncianteId(d.getIdDenunciante());
        dto.setDenuncianteNombre(resolverUsuario(d.getIdDenunciante(), usuarioCache));
        dto.setAlojamientoId(d.getIdAlojamiento());
        dto.setAlojamientoNombre(resolverAlojamiento(d.getIdAlojamiento(), aloCache));
        dto.setAnfitrionId(d.getIdAnfitrion());
        dto.setAnfitrionNombre(resolverUsuario(d.getIdAnfitrion(), usuarioCache));
        dto.setMotivo(d.getMotivo());
        dto.setDescripcion(d.getDescripcion());
        dto.setEstado(d.getEstado());
        dto.setFecha(formatearFechaIso(d.getFecha()));
        return dto;
    }

    private String resolverAlojamiento(int alojamientoId, Map<Integer, Alojamiento> aloCache) {
        if (alojamientoId <= 0) return "";
        Alojamiento alo;
        if (aloCache.containsKey(alojamientoId)) {
            alo = aloCache.get(alojamientoId);
        } else {
            try {
                alo = alojamientoBL.obtenerPorId(alojamientoId);
            } catch (RuntimeException ex) {
                alo = null;
            }
            aloCache.put(alojamientoId, alo);
        }
        return alo != null && alo.getNombre() != null ? alo.getNombre() : "";
    }

    private String resolverUsuario(int idUsuario, Map<Integer, String> usuarioCache) {
        if (idUsuario <= 0) return "";
        if (usuarioCache.containsKey(idUsuario)) {
            return usuarioCache.get(idUsuario);
        }
        String nombre = "";
        try {
            Usuario u = usuarioBL.obtenerPorId(idUsuario);
            if (u != null) {
                String n = u.getNombre() != null ? u.getNombre() : "";
                String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
                nombre = (n + " " + ap).trim();
            }
        } catch (RuntimeException ex) {
            nombre = "";
        }
        usuarioCache.put(idUsuario, nombre);
        return nombre;
    }

    /** Formatea a ISO "yyyy-MM-dd"; null -> null. */
    private static String formatearFechaIso(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(fecha);
    }
}
