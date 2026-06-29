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

import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;
import pe.edu.pe.pucp.proyecto.users.EstadoUsuario;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.validacion.DocumentoValidacion;
import pe.edu.pe.pucp.proyecto.validacion.bl.DocumentoValidacionBL;
import pe.edu.pe.pucp.proyecto.validacion.implbl.DocumentoValidacionBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.DocumentoValidacionDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Recurso REST de la validación documentaria (RF02). El usuario sube su DNI/pasaporte y el
 * admin lo aprueba o rechaza. La fuente de verdad del "usuario verificado" es la columna
 * usuario.estado_validacion (PENDIENTE / APROBADO / RECHAZADO), que aquí se sincroniza con la
 * decisión: al aprobar, el usuario pasa además a EstadoUsuario.DISPONIBLE.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/ValidacionRS
 */
@Path("ValidacionRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ValidacionRS {

    private final DocumentoValidacionBL validacionBL = new DocumentoValidacionBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /**
     * GET ValidacionRS/usuario/{id} -> el documento (estado) del usuario, para que la página
     * "Mis documentos" muestre si está PENDIENTE/APROBADO/RECHAZADO. 204 si no ha subido nada.
     */
    @GET
    @Path("usuario/{id}")
    public Response obtenerDeUsuario(@PathParam("id") int idUsuario) {
        DocumentoValidacion d = validacionBL.obtenerPorUsuario(idUsuario);
        if (d == null) {
            return Response.noContent().build();
        }
        return Response.ok(toDTO(d, true)).build();
    }

    /** GET ValidacionRS/pendientes -> cola de revisión del admin (con datos del usuario). */
    @GET
    @Path("pendientes")
    public List<DocumentoValidacionDTO> listarPendientes() {
        List<DocumentoValidacionDTO> salida = new ArrayList<>();
        for (DocumentoValidacion d : validacionBL.listarPendientes()) {
            salida.add(toDTO(d, true));
        }
        return salida;
    }

    /**
     * POST ValidacionRS -> el usuario sube su documento.
     * Body { usuarioId, tipoDocumento, numeroDocumento, archivoBase64 }. Guarda el documento
     * (PENDIENTE) y deja al usuario en estado_validacion = PENDIENTE.
     */
    @POST
    public Response subir(DocumentoValidacionDTO dto) {
        if (dto == null || dto.getUsuarioId() <= 0 || dto.getTipoDocumento() == null
                || dto.getNumeroDocumento() == null || dto.getArchivoBase64() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"usuarioId, tipoDocumento, numeroDocumento y archivoBase64 son obligatorios\"}")
                    .build();
        }
        try {
            DocumentoValidacion d = new DocumentoValidacion();
            d.setIdUsuario(dto.getUsuarioId());
            d.setTipoDocumento(dto.getTipoDocumento().trim().toUpperCase());
            d.setNumeroDocumento(dto.getNumeroDocumento().trim());
            d.setArchivoBase64(dto.getArchivoBase64());
            int id = validacionBL.subir(d);
            d.setIdDocumento(id);

            // El usuario vuelve a "pendiente de validación" mientras el admin revisa.
            Usuario u = usuarioBL.obtenerPorId(dto.getUsuarioId());
            if (u != null) {
                u.setEstadoValidacion(DocumentoValidacion.PENDIENTE);
                usuarioBL.modificar(u);
            }

            return Response.status(Response.Status.CREATED).entity(toDTO(d, false)).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    /**
     * PUT ValidacionRS/{id}/decision -> el admin aprueba o rechaza.
     * Body { estado: "APROBADO"|"RECHAZADO", motivoRechazo, adminId }.
     * APROBADO: el usuario pasa a estado_validacion=APROBADO y EstadoUsuario.DISPONIBLE, y se
     * copian su tipo/numero de documento al perfil. RECHAZADO: estado_validacion=RECHAZADO.
     */
    @PUT
    @Path("{id}/decision")
    public Response decidir(@PathParam("id") int idDocumento, DocumentoValidacionDTO dto) {
        if (dto == null || dto.getEstado() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"estado es obligatorio (APROBADO/RECHAZADO)\"}").build();
        }
        try {
            DocumentoValidacion doc = validacionBL.obtenerPorId(idDocumento);
            if (doc == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Documento no encontrado\"}").build();
            }
            String estado = dto.getEstado().trim().toUpperCase();
            validacionBL.decidir(idDocumento, estado, dto.getMotivoRechazo(), dto.getAdminId());

            // Sincroniza el estado del usuario con la decisión.
            Usuario u = usuarioBL.obtenerPorId(doc.getIdUsuario());
            if (u != null) {
                if (DocumentoValidacion.APROBADO.equals(estado)) {
                    u.setEstadoValidacion(DocumentoValidacion.APROBADO);
                    u.setEstadoActual(EstadoUsuario.DISPONIBLE);
                    aplicarDocumentoAlPerfil(u, doc);
                } else {
                    u.setEstadoValidacion(DocumentoValidacion.RECHAZADO);
                }
                usuarioBL.modificar(u);
            }

            DocumentoValidacion actualizado = validacionBL.obtenerPorId(idDocumento);
            return Response.ok(toDTO(actualizado, false)).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}").build();
        }
    }

    // ------------------------------------------------------------------

    /** Copia el tipo/numero del documento aprobado al perfil del usuario (si el tipo es válido). */
    private void aplicarDocumentoAlPerfil(Usuario u, DocumentoValidacion doc) {
        if (doc.getNumeroDocumento() != null) {
            u.setNumeroDocumento(doc.getNumeroDocumento());
        }
        if (doc.getTipoDocumento() != null) {
            try {
                u.setTipoDocumento(TipoDocumento.valueOf(doc.getTipoDocumento().trim().toUpperCase()));
            } catch (IllegalArgumentException ignore) {
                // Tipo no mapeable al enum: dejamos el documento sin tipar en el perfil.
            }
        }
    }

    private DocumentoValidacionDTO toDTO(DocumentoValidacion d, boolean resolverUsuario) {
        DocumentoValidacionDTO dto = new DocumentoValidacionDTO();
        dto.setId(d.getIdDocumento());
        dto.setUsuarioId(d.getIdUsuario());
        dto.setTipoDocumento(d.getTipoDocumento());
        dto.setNumeroDocumento(d.getNumeroDocumento());
        dto.setArchivoBase64(d.getArchivoBase64());
        dto.setEstado(d.getEstado());
        dto.setMotivoRechazo(d.getMotivoRechazo());
        dto.setAdminId(d.getIdAdminValidador());
        dto.setFechaSubida(formatearFecha(d.getFechaSubida()));
        dto.setFechaRevision(formatearFecha(d.getFechaRevision()));
        if (resolverUsuario) {
            try {
                Usuario u = usuarioBL.obtenerPorId(d.getIdUsuario());
                if (u != null) {
                    String n = u.getNombre() != null ? u.getNombre() : "";
                    String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
                    dto.setUsuarioNombre((n + " " + ap).trim());
                    dto.setUsuarioCorreo(u.getCorreo());
                }
            } catch (RuntimeException ignore) {
                // Si no se puede resolver el usuario, el DTO viaja sin nombre/correo (no rompe el panel).
            }
        }
        return dto;
    }

    /** Formatea a ISO "yyyy-MM-dd HH:mm"; null -> null. */
    private static String formatearFecha(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha);
    }
}
