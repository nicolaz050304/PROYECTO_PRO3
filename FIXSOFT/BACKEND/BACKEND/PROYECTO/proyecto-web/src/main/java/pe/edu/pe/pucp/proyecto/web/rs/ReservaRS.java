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
import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;
import pe.edu.pe.pucp.proyecto.bloqueo.bl.BloqueoFechaBL;
import pe.edu.pe.pucp.proyecto.bloqueo.implbl.BloqueoFechaBLImpl;
import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.MovimientoCuentaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.CuentaBancariaBLImpl;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.MovimientoCuentaBLImpl;
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
    private final BloqueoFechaBL bloqueoBL = new BloqueoFechaBLImpl();
    private final CuentaBancariaBL cuentaBL = new CuentaBancariaBLImpl();
    private final MovimientoCuentaBL movimientoBL = new MovimientoCuentaBLImpl();
    private final pe.edu.pe.pucp.proyecto.auditoria.bl.AuditoriaEstadoBL auditoriaBL =
            new pe.edu.pe.pucp.proyecto.auditoria.implbl.AuditoriaEstadoBLImpl();

    /** Comisión de la plataforma sobre el monto de la reserva (RF13). El anfitrión cobra el resto. */
    private static final double COMISION = 0.10;

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
        // Filtrado en SQL (id_invitado): antes traía TODAS las reservas y filtraba en memoria.
        List<Reserva> lista = reservaBL.listarPorInvitado(usuarioId);
        if (lista != null) {
            for (Reserva r : lista) {
                salida.add(ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, aloCache, usuarioCache));
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
        // Filtrado en SQL (JOIN reserva-alojamiento por dueño): antes traía TODAS las reservas y,
        // por cada una, consultaba el alojamiento para ver el dueño (N+1). Ahora la BD devuelve
        // directamente solo las reservas del anfitrión.
        List<Reserva> lista = reservaBL.listarPorAnfitrion(anfitrionId);
        if (lista != null) {
            for (Reserva r : lista) {
                salida.add(ReservaMapper.toDTO(r, alojamientoBL, usuarioBL, aloCache, usuarioCache));
            }
        }
        return salida;
    }

    /**
     * GET ReservaRS/ocupadas/{idAlojamiento} -> rangos de fechas OCUPADAS de un alojamiento.
     * Une dos fuentes: (1) reservas CONFIRMADA o PENDIENTE (las CANCELADA/FINALIZADA ya no ocupan;
     * el filtro lo aplica el DAO) y (2) los bloqueos manuales del anfitrión (RF30). Devuelve
     * únicamente las fechas (RangoFechaDTO), sin datos del huésped: es la info mínima para que el
     * calendario del frontend bloquee esos días, sin distinguir el origen del rango.
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
        // RF30: los bloqueos del anfitrión cuentan como ocupados para el calendario del huésped.
        for (BloqueoFecha b : bloqueoBL.listarPorAlojamiento(idAlojamiento)) {
            salida.add(new RangoFechaDTO(
                    formatearFechaIso(b.getFechaInicio()),
                    formatearFechaIso(b.getFechaFin())));
        }
        return salida;
    }

    /**
     * RF15: acredita al anfitrión (dueño del alojamiento) su ganancia por una reserva confirmada.
     * Paga monto - COMISIÓN sobre su cuenta bancaria (la primera verificada; si ninguna, la primera).
     * Si el anfitrión no tiene cuenta registrada, no se abona (debe registrar una para cobrar).
     */
    private void abonarAnfitrionPorReserva(int alojamientoId, double total, int reservaId) {
        if (alojamientoId <= 0 || total <= 0) {
            return;
        }
        Alojamiento alo = alojamientoBL.obtenerPorId(alojamientoId);
        if (alo == null || alo.getDuenho() == null) {
            return;
        }
        int idAnfitrion = alo.getDuenho().getIdUsuario();
        List<CuentaBancaria> cuentas = cuentaBL.listarPorUsuario(idAnfitrion);
        if (cuentas == null || cuentas.isEmpty()) {
            return; // sin cuenta no se puede abonar; el anfitrión debe registrar una
        }
        // Prioridad del destino del abono: (1) la cuenta PRINCIPAL de cobro elegida por el anfitrión,
        // (2) si no hay, la primera verificada, (3) si tampoco, la primera registrada.
        CuentaBancaria destino = null;
        for (CuentaBancaria c : cuentas) {
            if (c.isPrincipal()) { destino = c; break; }
        }
        if (destino == null) {
            for (CuentaBancaria c : cuentas) {
                if (c.isVerificada()) { destino = c; break; }
            }
        }
        if (destino == null) {
            destino = cuentas.get(0);
        }
        double ganancia = Math.round((total * (1 - COMISION)) * 100.0) / 100.0;
        String desc = "Abono por reserva #" + reservaId
                + (alo.getNombre() != null ? " · " + alo.getNombre() : "");
        movimientoBL.abonar(destino.getIdCuenta(), ganancia, desc, reservaId);
        // Nota: la notificación de "Pago recibido" (categoría PAGO) al anfitrión la emite PagoBLImpl
        // al registrarse el pago; no se duplica aquí.
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
        // RNF09: registra el estado inicial de la reserva (transición desde "nada" -> estado inicial).
        auditoriaBL.registrar(pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado.ENTIDAD_RESERVA,
                nuevoId, "estado", null, nombreEstado(entidad.getEstadoReserva()), "Reserva creada");
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

        // RNF09: si el estado de la reserva CAMBIÓ, lo registramos en la bitácora de auditoría.
        if (estadoAnterior != entidad.getEstadoReserva()) {
            auditoriaBL.registrar(pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado.ENTIDAD_RESERVA,
                    id, "estado", nombreEstado(estadoAnterior),
                    nombreEstado(entidad.getEstadoReserva()), null);
        }

        // EVENTO 1 (RF18): si la reserva PASA a CONFIRMADA (antes no lo estaba), notificamos al HUÉSPED.
        // La notificación es secundaria: va en try/catch para no romper la confirmación si algo falla.
        if (entidad.getEstadoReserva() == EstadoReserva.CONFIRMADA
                && estadoAnterior != EstadoReserva.CONFIRMADA && actual != null) {
            int idHuesped = actual.getInvitado() != null ? actual.getInvitado().getIdUsuario() : 0;
            if (idHuesped > 0) {
                try {
                    notificacionBL.crear("Reserva confirmada",
                            "Tu reserva ha sido confirmada por el anfitrión.", idHuesped, "RESERVA");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // RF15: al confirmar, se ACREDITA al anfitrión su ganancia (monto - comisión) en su cuenta
            // bancaria. Va en try/catch porque el abono es secundario y no debe romper la confirmación.
            // El guard (transición a CONFIRMADA) evita doble abono al re-guardar una reserva ya confirmada.
            try {
                double total = entidad.getMontoTotal() > 0 ? entidad.getMontoTotal() : actual.getMontoTotal();
                int alojamientoId = actual.getAlojamiento() != null ? actual.getAlojamiento().getIdAlojamiento() : 0;
                abonarAnfitrionPorReserva(alojamientoId, total, id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // RF15: si la reserva PASA a CANCELADA (antes no lo estaba) y tuvo un abono, se REEMBOLSA
        // (se debita del anfitrión la ganancia recibida). Idempotente y no-op si no hubo abono.
        if (entidad.getEstadoReserva() == EstadoReserva.CANCELADA
                && estadoAnterior != EstadoReserva.CANCELADA) {
            try { movimientoBL.reembolsarPorReserva(id); } catch (Exception ex) { ex.printStackTrace(); }
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
        EstadoReserva estadoPrevio = r.getEstadoReserva();
        reservaBL.eliminar(r);
        // RNF09: la baja lógica es una transición a CANCELADA; queda registrada en la auditoría.
        auditoriaBL.registrar(pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado.ENTIDAD_RESERVA,
                id, "estado", nombreEstado(estadoPrevio), "CANCELADA", "Reserva cancelada");
        // RF15: cancelar (baja lógica) también revierte el abono al anfitrión si lo hubo. Secundario.
        try { movimientoBL.reembolsarPorReserva(id); } catch (Exception ex) { ex.printStackTrace(); }
        return Response.noContent().build();
    }

    /** Nombre del estado de reserva (enum -> String); null -> null. Para la bitácora de auditoría. */
    private static String nombreEstado(EstadoReserva e) {
        return e != null ? e.name() : null;
    }
}
