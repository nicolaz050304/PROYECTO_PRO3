package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;
import pe.edu.pe.pucp.proyecto.auditoria.bl.AuditoriaEstadoBL;
import pe.edu.pe.pucp.proyecto.auditoria.implbl.AuditoriaEstadoBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.AuditoriaEstadoDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.AuditoriaEstadoMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de la bitácora de auditoría (RNF09). SOLO lectura: el historial se escribe desde la
 * lógica de negocio (no hay endpoints de escritura aquí). Es información sensible: el AuthFilter
 * restringe estas rutas al rol ADMINISTRADOR.
 *
 * Base: http://localhost:8080/BunkiBackend/webresources/AuditoriaRS
 */
@Path("AuditoriaRS")
@Produces(MediaType.APPLICATION_JSON)
public class AuditoriaRS {

    private final AuditoriaEstadoBL auditoriaBL = new AuditoriaEstadoBLImpl();

    /** GET AuditoriaRS?limite=N -> últimos N registros (vista global del admin). Default 200. */
    @GET
    public List<AuditoriaEstadoDTO> listarRecientes(@QueryParam("limite") int limite) {
        return mapear(auditoriaBL.listarRecientes(limite > 0 ? limite : 200));
    }

    /** GET AuditoriaRS/reserva/{id} -> historial cronológico de una reserva. */
    @GET
    @Path("reserva/{id}")
    public List<AuditoriaEstadoDTO> historialReserva(@PathParam("id") int id) {
        return mapear(auditoriaBL.listarPorEntidad(AuditoriaEstado.ENTIDAD_RESERVA, id));
    }

    /** GET AuditoriaRS/usuario/{id} -> historial cronológico de un perfil de usuario. */
    @GET
    @Path("usuario/{id}")
    public List<AuditoriaEstadoDTO> historialUsuario(@PathParam("id") int id) {
        return mapear(auditoriaBL.listarPorEntidad(AuditoriaEstado.ENTIDAD_USUARIO, id));
    }

    private static List<AuditoriaEstadoDTO> mapear(List<AuditoriaEstado> registros) {
        List<AuditoriaEstadoDTO> salida = new ArrayList<>();
        if (registros != null) {
            for (AuditoriaEstado a : registros) salida.add(AuditoriaEstadoMapper.toDTO(a));
        }
        return salida;
    }
}
