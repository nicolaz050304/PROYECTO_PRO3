package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
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
                // Excluye los de baja lógica / pausados (disponibilidad=0) y los que aún no
                // pasaron la moderación del admin (estadoValidacion != APROBADO): un PENDIENTE
                // o RECHAZADO no aparece en el catálogo público. El panel del anfitrión
                // (listarPorAnfitrion) sí los sigue viendo.
                if (al == null || !al.isDisponibilidad()
                        || !"APROBADO".equalsIgnoreCase(al.getEstadoValidacion())) {
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
                    // Igual que el catálogo: solo APROBADO y disponible llega al público.
                    if (al == null || !al.isDisponibilidad()
                            || !"APROBADO".equalsIgnoreCase(al.getEstadoValidacion())) {
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

    /**
     * GET AlojamientoRS/top?criterio=...&limite=5 -> TOP de alojamientos (RF22).
     * El ranking se calcula en el BACKEND (lógica de negocio + eficiencia: el orden sale del SQL,
     * no se trae todo al frontend). Criterios: "calificacion" (promedio de calificación de reseñas),
     * "reservas" (conteo de reservas) o "resenas" (conteo de reseñas activas). El DTO ya trae rating
     * y totalResenas (calculados desde resenasCache), así que el frontend muestra la métrica relevante.
     */
    @GET
    @Path("top")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AlojamientoDTO> top(@QueryParam("criterio") @DefaultValue("calificacion") String criterio,
                                    @QueryParam("limite") @DefaultValue("5") int limite) {
        Map<Integer, String> cache = new HashMap<>();
        Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> top = alojamientoBL.topPorCriterio(criterio, limite);
        for (Alojamiento al : top) {
            // Mismo mapeo/caché de reseñas que el catálogo: rating/totalResenas reales para cada alojamiento.
            salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache, resenasCache));
        }
        return salida;
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

    /**
     * GET AlojamientoRS/pendientes -> alojamientos en espera de moderación (estadoValidacion=PENDIENTE).
     * Para el panel del administrador. No existe finder en la BL/DAO: se resuelve filtrando
     * listarTodos() por estadoValidacion, sin tocar la capa de datos (mismo enfoque que
     * listarPorAnfitrion). El DTO ya transporta estadoValidacion.
     */
    @GET
    @Path("pendientes")
    public List<AlojamientoDTO> listarPendientes() {
        Map<Integer, String> cache = new HashMap<>();
        Map<Integer, double[]> resenasCache = new ResenhaBLImpl().calificacionesPorAlojamiento();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> lista = alojamientoBL.listarTodos();
        if (lista != null) {
            for (Alojamiento al : lista) {
                if (al != null && "PENDIENTE".equalsIgnoreCase(al.getEstadoValidacion())) {
                    salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache, resenasCache));
                }
            }
        }
        return salida;
    }

    /**
     * PUT AlojamientoRS/{id}/validacion -> el admin aprueba o rechaza un alojamiento.
     * Recibe { "estado": "APROBADO" | "RECHAZADO" }. Mismo patrón que UsuarioRS/{id}/estado:
     * carga la entidad real desde la BD, le cambia SOLO el estadoValidacion y persiste con
     * modificar() (el UPDATE del DAO ya guarda estado_validacion). No pasa por toEntity, así que
     * no se reinicia el estado ni se pisan los demás campos.
     */
    @PUT
    @Path("{id}/validacion")
    public Response validar(@PathParam("id") int id, ValidacionRequest req) {
        Alojamiento al = alojamientoBL.obtenerPorId(id);
        if (al == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String estado = (req != null && req.getEstado() != null) ? req.getEstado().toUpperCase() : "";
        if (!"APROBADO".equals(estado) && !"RECHAZADO".equals(estado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"estado debe ser APROBADO o RECHAZADO\"}")
                    .build();
        }
        al.setEstadoValidacion(estado);
        alojamientoBL.modificar(al);
        return Response.ok(AlojamientoMapper.toDTO(al, usuarioBL, new HashMap<>())).build();
    }

    /** Body del PUT {id}/validacion: { "estado": "APROBADO" | "RECHAZADO" }. */
    public static class ValidacionRequest {
        private String estado;
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
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
        // PRESERVAR la moderación: editar un alojamiento NO debe des-aprobarlo. toEntity siempre pone
        // estadoValidacion=PENDIENTE (seguro para CREAR: el cliente no se auto-aprueba), pero al EDITAR
        // tomamos el estado real desde la BD (autoritativo). Así un APROBADO sigue en el catálogo tras editar.
        Alojamiento actual = alojamientoBL.obtenerPorId(id);
        if (actual != null && actual.getEstadoValidacion() != null) {
            entidad.setEstadoValidacion(actual.getEstadoValidacion());
        }
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
