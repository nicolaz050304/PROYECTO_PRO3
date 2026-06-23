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
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.notif.bl.NotificacionBL;
import pe.edu.pe.pucp.proyecto.notif.implbl.NotificacionBLImpl;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.RangoFechaDTO;
import pe.edu.pe.pucp.proyecto.web.dto.ReservaDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.ReservaMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final NotificacionBL notificacionBL = new NotificacionBLImpl();

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

    /**
     * GET ReservaRS/ocupadas/{idAlojamiento} -> rangos de fechas OCUPADAS de un alojamiento.
     * Solo cuentan las reservas CONFIRMADA o PENDIENTE (las CANCELADA/FINALIZADA ya no ocupan);
     * el filtro lo aplica el DAO. Devuelve únicamente las fechas (RangoFechaDTO), sin datos del
     * huésped: es la info mínima para que el calendario del frontend bloquee esos días.
     */
    @GET
    @Path("ocupadas/{idAlojamiento}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RangoFechaDTO> listarOcupadas(@PathParam("idAlojamiento") int idAlojamiento) {
        List<RangoFechaDTO> salida = new ArrayList<>();
        List<Reserva> ocupadas = reservaBL.listarOcupadasPorAlojamiento(idAlojamiento);
        if (ocupadas != null) {
            for (Reserva r : ocupadas) {
                salida.add(new RangoFechaDTO(
                        formatearFechaIso(r.getFechaInicio()),
                        formatearFechaIso(r.getFechaFin())));
            }
        }
        return salida;
    }

    /** Formatea a ISO "yyyy-MM-dd" (sin hora ni 'Z'), igual que el resto de fechas expuestas; null -> null. */
    private static String formatearFechaIso(Date fecha) {
        if (fecha == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(fecha);
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
        // Cargamos el estado ANTERIOR antes de modificar, para detectar la transición a CONFIRMADA
        // (el huésped del id viene poblado en la reserva cargada por la BL).
        Reserva actual = reservaBL.obtenerPorId(id);
        EstadoReserva estadoAnterior = actual != null ? actual.getEstadoReserva() : null;

        Reserva entidad = ReservaMapper.toEntity(dto);
        entidad.setIdReserva(id);
        reservaBL.modificar(entidad);

        // EVENTO 1 (RF18): si la reserva PASA a CONFIRMADA (antes no lo estaba), notificamos al HUÉSPED.
        // La notificación es secundaria: va en try/catch para no romper la confirmación si algo falla.
        if (entidad.getEstadoReserva() == EstadoReserva.CONFIRMADA
                && estadoAnterior != EstadoReserva.CONFIRMADA && actual != null) {
            int idHuesped = actual.getInvitado() != null ? actual.getInvitado().getIdUsuario() : 0;
            if (idHuesped > 0) {
                try {
                    notificacionBL.crear("Reserva confirmada",
                            "Tu reserva ha sido confirmada por el anfitrión.", idHuesped);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

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
