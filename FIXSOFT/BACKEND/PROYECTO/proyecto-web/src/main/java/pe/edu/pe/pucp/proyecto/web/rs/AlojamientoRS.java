package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.review.implbl.ResenhaBLImpl;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.AlojamientoDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.AlojamientoMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de Alojamiento. Solo expone DTOs (nunca entidades del modelo) y
 * delega toda la lógica en la BL existente: ningún acceso directo a datos.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/AlojamientoRS
 */
@Path("AlojamientoRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlojamientoRS {

    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET AlojamientoRS -> catálogo público: SOLO alojamientos disponibles (disponibilidad=1). */
    @GET
    public List<AlojamientoDTO> listar() {
        Map<Integer, String> cache = new HashMap<>();
        Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> lista = alojamientoBL.listarTodos();
        if (lista != null) {
            for (Alojamiento al : lista) {
                // Excluye los de baja lógica / pausados (disponibilidad=0); el panel del
                // anfitrión (listarPorAnfitrion) sí los sigue viendo.
                if (al == null || !al.isDisponibilidad()) {
                    continue;
                }
                salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache, resenasCache));
            }
        }
        return salida;
    }

    /**
     * GET AlojamientoRS/disponibles?entrada=yyyy-MM-dd&salida=yyyy-MM-dd
     * Catálogo filtrado por fechas: excluye los alojamientos con una reserva viva
     * (PENDIENTE/CONFIRMADA) que se solape con [entrada, salida]. Si faltan fechas, la BL
     * devuelve todo (búsqueda sin fechas no filtra). Mismo mapeo/cachés que el catálogo normal.
     */
    @GET
    @Path("disponibles")
    public List<AlojamientoDTO> listarDisponibles(@QueryParam("entrada") String entrada,
                                                  @QueryParam("salida") String salida) {
        List<AlojamientoDTO> salidaDTO = new ArrayList<>();
        try {
            LocalDate e = (entrada != null && !entrada.isEmpty()) ? LocalDate.parse(entrada) : null;
            LocalDate s = (salida != null && !salida.isEmpty()) ? LocalDate.parse(salida) : null;

            Map<Integer, String> cache = new HashMap<>();
            Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
            List<Alojamiento> lista = alojamientoBL.listarDisponibles(e, s);
            if (lista != null) {
                for (Alojamiento al : lista) {
                    if (al == null || !al.isDisponibilidad()) {
                        continue;
                    }
                    salidaDTO.add(AlojamientoMapper.toDTO(al, usuarioBL, cache, resenasCache));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return salidaDTO;
    }

    /** GET AlojamientoRS/{id} -> uno; 404 si no existe. */
    @GET
    @Path("{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        Alojamiento al = alojamientoBL.obtenerPorId(id);
        if (al == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
        return Response.ok(AlojamientoMapper.toDTO(al, usuarioBL, new HashMap<>(), resenasCache)).build();
    }

    /**
     * GET AlojamientoRS/anfitrion/{id} -> alojamientos de un anfitrión.
     * No existe finder en la BL/DAO: se resuelve filtrando listarTodos() por
     * duenho.idUsuario, sin tocar la capa de datos.
     */
    @GET
    @Path("anfitrion/{id}")
    public List<AlojamientoDTO> listarPorAnfitrion(@PathParam("id") int anfitrionId) {
        Map<Integer, String> cache = new HashMap<>();
        Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> lista = alojamientoBL.listarTodos();
        if (lista != null) {
            for (Alojamiento al : lista) {
                if (al.getDuenho() != null && al.getDuenho().getIdUsuario() == anfitrionId) {
                    salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache, resenasCache));
                }
            }
        }
        return salida;
    }

    /** POST AlojamientoRS -> crea; devuelve el DTO creado con su id. */
    @POST
    public Response crear(AlojamientoDTO dto) {
        Alojamiento entidad = AlojamientoMapper.toEntity(dto);
        int nuevoId = alojamientoBL.insertar(entidad);
        entidad.setIdAlojamiento(nuevoId);
        return Response.status(Response.Status.CREATED)
                .entity(AlojamientoMapper.toDTO(entidad, usuarioBL, new HashMap<>()))
                .build();
    }

    /** PUT AlojamientoRS/{id} -> modifica. */
    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, AlojamientoDTO dto) {
        Alojamiento entidad = AlojamientoMapper.toEntity(dto);
        entidad.setIdAlojamiento(id);
        alojamientoBL.modificar(entidad);
        return Response.ok(AlojamientoMapper.toDTO(entidad, usuarioBL, new HashMap<>())).build();
    }

    /** DELETE AlojamientoRS/{id} -> baja lógica vía BL. */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        Alojamiento al = alojamientoBL.obtenerPorId(id);
        if (al == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        alojamientoBL.eliminar(al);
        return Response.noContent().build();
    }
}
