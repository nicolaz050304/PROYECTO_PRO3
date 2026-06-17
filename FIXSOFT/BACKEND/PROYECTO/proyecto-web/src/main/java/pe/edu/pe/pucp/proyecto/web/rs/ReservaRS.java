package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.ReservaDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.ReservaMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de Reserva. Solo expone DTOs (nunca la entidad Reserva, que tiene ciclos)
 * y delega en la BL existente: ningún acceso directo a datos. Los finders por usuario y
 * anfitrión no existen en la BL/DAO; se resuelven aquí filtrando listarTodos().
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/ReservaRS
 */
@Path("ReservaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaRS {

    private final ReservaBL reservaBL = new ReservaBLImpl();
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET ReservaRS -> todas las reservas. */
    @GET
    public List<ReservaDTO> listar() {
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ReservaDTO> salida = new ArrayList<>();
        List<Reserva> lista = reservaBL.listarTodos();
        if (lista != null) {
            for (Reserva r : lista) {
                salida.add(ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, aloCache, usuarioCache));
            }
        }
        return salida;
    }

    /** GET ReservaRS/{id} -> una; 404 si no existe. */
    @GET
    @Path("{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        Reserva r = reservaBL.obtenerPorId(id);
        if (r == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ReservaDTO dto = ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, new HashMap<>(), new HashMap<>());
        return Response.ok(dto).build();
    }

    /**
     * GET ReservaRS/usuario/{id} -> reservas de un invitado.
     * Filtra listarTodos() por invitado.idUsuario == id.
     */
    @GET
    @Path("usuario/{id}")
    public List<ReservaDTO> listarPorUsuario(@PathParam("id") int usuarioId) {
        reservaBL.finalizarReservasVencidas();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ReservaDTO> salida = new ArrayList<>();
        List<Reserva> lista = reservaBL.listarTodos();
        if (lista != null) {
            for (Reserva r : lista) {
                if (r.getInvitado() != null && r.getInvitado().getIdUsuario() == usuarioId) {
                    salida.add(ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, aloCache, usuarioCache));
                }
            }
        }
        return salida;
    }

    /**
     * GET ReservaRS/anfitrion/{id} -> reservas cuyos alojamientos pertenecen a ese anfitrión.
     * Para cada reserva, cruza su alojamientoId con AlojamientoBL para ver el dueño.
     * Cachea los alojamientos por id (anti N+1).
     */
    @GET
    @Path("anfitrion/{id}")
    public List<ReservaDTO> listarPorAnfitrion(@PathParam("id") int anfitrionId) {
        reservaBL.finalizarReservasVencidas();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ReservaDTO> salida = new ArrayList<>();
        List<Reserva> lista = reservaBL.listarTodos();
        if (lista != null) {
            for (Reserva r : lista) {
                int alojamientoId = r.getAlojamiento() != null ? r.getAlojamiento().getIdAlojamiento() : 0;
                if (alojamientoId <= 0) {
                    continue;
                }
                try {
                    Alojamiento alo = aloCache.get(alojamientoId);
                    if (alo == null && !aloCache.containsKey(alojamientoId)) {
                        alo = alojamientoBL.obtenerPorId(alojamientoId);
                        aloCache.put(alojamientoId, alo);
                    }
                    if (alo != null && alo.getDuenho() != null
                            && alo.getDuenho().getIdUsuario() == anfitrionId) {
                        salida.add(ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, aloCache, usuarioCache));
                    }
                } catch (RuntimeException ex) {
                    // Defensivo: si el lookup falla, esa reserva simplemente no se incluye.
                }
            }
        }
        return salida;
    }

    /** POST ReservaRS -> crea; devuelve el DTO creado con su id. */
    @POST
    public Response crear(ReservaDTO dto) {
        Reserva entidad = ReservaMapper.toEntity(dto);
        int nuevoId = reservaBL.insertar(entidad);
        entidad.setIdReserva(nuevoId);
        ReservaDTO creado = ReservaMapper.toDTO(entidad, alojamientoBL, usuarioBL, new HashMap<>(), new HashMap<>());
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    /** PUT ReservaRS/{id} -> modifica. */
    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, ReservaDTO dto) {
        Reserva entidad = ReservaMapper.toEntity(dto);
        entidad.setIdReserva(id);
        reservaBL.modificar(entidad);
        ReservaDTO actualizado = ReservaMapper.toDTO(entidad, alojamientoBL, usuarioBL, new HashMap<>(), new HashMap<>());
        return Response.ok(actualizado).build();
    }

    /** DELETE ReservaRS/{id} -> baja lógica (estado CANCELADA) vía BL. */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        Reserva r = reservaBL.obtenerPorId(id);
        if (r == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        reservaBL.eliminar(r);
        return Response.noContent().build();
    }
}
