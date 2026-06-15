package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.Casa;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.web.dto.ReservaDTO;

import java.util.Map;

/**
 * Conversión Reserva (entidad) <-> ReservaDTO (contrato plano). Tolera null en todo.
 * El DAO solo carga ids en las referencias (invitado.idUsuario, alojamiento.idAlojamiento),
 * así que el nombre/dirección del alojamiento y el nombre del anfitrión se resuelven con
 * lookups a AlojamientoBL/UsuarioBL, cacheados por request para evitar N+1.
 */
public final class ReservaMapper {

    private ReservaMapper() {
    }

    // ============================================================
    // Entidad -> DTO
    // ============================================================

    /** Variante simple: sin lookups (nombre alojamiento/anfitrión quedan ""). */
    public static ReservaDTO toDTO(Reserva r) {
        return toDTO(r, null, null, null, null);
    }

    /**
     * Mapeo completo. {@code aloCache} y {@code usuarioCache} son cachés por request
     * (pueden ser null). Si se pasan {@code alojamientoBL}/{@code usuarioBL}, se resuelven
     * los nombres reales; si no, quedan "".
     */
    public static ReservaDTO toDTO(Reserva r,
                                   AlojamientoBL alojamientoBL,
                                   UsuarioBL usuarioBL,
                                   Map<Integer, Alojamiento> aloCache,
                                   Map<Integer, String> usuarioCache) {
        if (r == null) {
            return null;
        }

        ReservaDTO dto = new ReservaDTO();
        dto.setId(r.getIdReserva());
        dto.setFechaEntrada(r.getFechaInicio());
        dto.setFechaSalida(r.getFechaFin());
        dto.setTotal(r.getMontoTotal());
        dto.setEstado(estadoToString(r.getEstadoReserva()));

        int alojamientoId = r.getAlojamiento() != null ? r.getAlojamiento().getIdAlojamiento() : 0;
        dto.setAlojamientoId(alojamientoId);
        dto.setHuespedId(r.getInvitado() != null ? r.getInvitado().getIdUsuario() : 0);

        // No existe en el backend: default 1.
        dto.setNumHuespedes(1);

        // Lookup del alojamiento (nombre + dirección) y, en doble salto, del anfitrión.
        Alojamiento alo = buscarAlojamiento(alojamientoId, alojamientoBL, aloCache);
        dto.setAlojamientoNombre(alo != null && alo.getNombre() != null ? alo.getNombre() : "");
        dto.setUbicacion(alo != null && alo.getDireccion() != null ? alo.getDireccion() : "");
        dto.setAnfitrionNombre(resolverAnfitrionNombre(alo, usuarioBL, usuarioCache));

        return dto;
    }

    /** CONFIRMADA->"Confirmada", PENDIENTE->"Pendiente", CANCELADA->"Cancelada", FINALIZADA->"Completada". */
    private static String estadoToString(EstadoReserva estado) {
        if (estado == null) {
            return "";
        }
        return switch (estado) {
            case CONFIRMADA -> "Confirmada";
            case PENDIENTE -> "Pendiente";
            case CANCELADA -> "Cancelada";
            case FINALIZADA -> "Completada";
        };
    }

    private static Alojamiento buscarAlojamiento(int alojamientoId, AlojamientoBL alojamientoBL,
                                                 Map<Integer, Alojamiento> aloCache) {
        if (alojamientoBL == null || alojamientoId <= 0) {
            return null;
        }
        if (aloCache != null && aloCache.containsKey(alojamientoId)) {
            return aloCache.get(alojamientoId);
        }
        Alojamiento alo = null;
        try {
            alo = alojamientoBL.obtenerPorId(alojamientoId);
        } catch (RuntimeException ex) {
            alo = null; // sin BD / id inexistente -> el frontend tolera vacío
        }
        if (aloCache != null) {
            aloCache.put(alojamientoId, alo);
        }
        return alo;
    }

    private static String resolverAnfitrionNombre(Alojamiento alo, UsuarioBL usuarioBL,
                                                  Map<Integer, String> usuarioCache) {
        if (alo == null || usuarioBL == null || alo.getDuenho() == null) {
            return "";
        }
        int anfitrionId = alo.getDuenho().getIdUsuario();
        if (anfitrionId <= 0) {
            return "";
        }
        if (usuarioCache != null && usuarioCache.containsKey(anfitrionId)) {
            return usuarioCache.get(anfitrionId);
        }
        String nombre = "";
        try {
            Usuario u = usuarioBL.obtenerPorId(anfitrionId);
            if (u != null) {
                String n = u.getNombre() != null ? u.getNombre() : "";
                String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
                nombre = (n + " " + ap).trim();
            }
        } catch (RuntimeException ex) {
            nombre = "";
        }
        if (usuarioCache != null) {
            usuarioCache.put(anfitrionId, nombre);
        }
        return nombre;
    }

    // ============================================================
    // DTO -> Entidad (POST / PUT)
    // ============================================================

    /**
     * Mapea el DTO a una entidad Reserva con las referencias creadas SOLO por id
     * (igual que hace el DAO): Invitado(idUsuario=huespedId) y Casa(idAlojamiento=alojamientoId).
     *
     * TODO: el DTO no transporta moneda ni fechaContacto. moneda se fija por defecto a PEN
     * (el DAO la requiere no-nula al guardar); fechaContacto queda null. Las escrituras de
     * reserva aún no se cablean desde el frontend.
     */
    public static Reserva toEntity(ReservaDTO dto) {
        if (dto == null) {
            return null;
        }

        Reserva r = new Reserva();
        r.setIdReserva(dto.getId());
        r.setFechaInicio(dto.getFechaEntrada());
        r.setFechaFin(dto.getFechaSalida());
        r.setMontoTotal(dto.getTotal());
        r.setEstadoReserva(estadoFromString(dto.getEstado()));
        r.setMoneda(TipoMoneda.PEN); // default: el DTO no trae moneda y el DAO la exige no-nula

        // Referencias por id (la subclase Casa replica lo que hace el DAO).
        Invitado invitado = new Invitado();
        invitado.setIdUsuario(dto.getHuespedId());
        r.setInvitado(invitado);

        Casa alojamiento = new Casa();
        alojamiento.setIdAlojamiento(dto.getAlojamientoId());
        r.setAlojamiento(alojamiento);

        return r;
    }

    /** Inverso de estadoToString. "Completada"->FINALIZADA. Default: PENDIENTE. */
    private static EstadoReserva estadoFromString(String estado) {
        if (estado == null) {
            return EstadoReserva.PENDIENTE;
        }
        return switch (estado.trim().toLowerCase()) {
            case "confirmada" -> EstadoReserva.CONFIRMADA;
            case "cancelada" -> EstadoReserva.CANCELADA;
            case "completada", "finalizada" -> EstadoReserva.FINALIZADA;
            default -> EstadoReserva.PENDIENTE;
        };
    }
}
