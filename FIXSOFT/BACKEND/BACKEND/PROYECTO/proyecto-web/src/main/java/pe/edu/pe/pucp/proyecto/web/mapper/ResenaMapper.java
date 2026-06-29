package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.web.dto.ResenaDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Conversión Resenha (entidad) <-> ResenaDTO (contrato plano). Tolera null en todo.
 *
 * El DAO de Resenha solo carga el id de la reserva (reserva.idReserva); todo lo demás
 * (alojamientoId, nombre del autor) se resuelve con lookups cacheados por request para
 * evitar N+1:
 *   - reservaCache:  ReservaBL.obtenerPorId(reservaId)      -> invitado + alojamientoId
 *   - aloCache:      AlojamientoBL.obtenerPorId(alojamientoId) -> dueño (anfitrión)
 *   - usuarioCache:  UsuarioBL.obtenerPorId(usuarioId)       -> nombre + apellido (texto)
 *
 * NO toca el DAO ni la BL: solo delega en métodos ya existentes.
 */
public final class ResenaMapper {

    /** Patrón ISO estable para transportar la fecha como dato (no como texto bonito). */
    private static final String PATRON_ISO = "yyyy-MM-dd";

    private ResenaMapper() {
    }

    // ============================================================
    // Entidad -> DTO
    // ============================================================

    /** Variante simple: sin lookups (autorNombre/alojamientoId quedan vacíos/0). */
    public static ResenaDTO toDTO(Resenha res) {
        return toDTO(res, null, null, null, null, null, null);
    }

    /**
     * Mapeo completo. Las cachés (pueden ser null) evitan repetir lookups dentro de un
     * mismo request. Si no se pasan las BL, autorNombre queda "" y alojamientoId 0.
     */
    public static ResenaDTO toDTO(Resenha res,
                                  ReservaBL reservaBL,
                                  AlojamientoBL alojamientoBL,
                                  UsuarioBL usuarioBL,
                                  Map<Integer, Reserva> reservaCache,
                                  Map<Integer, Alojamiento> aloCache,
                                  Map<Integer, String> usuarioCache) {
        if (res == null) {
            return null;
        }

        ResenaDTO dto = new ResenaDTO();
        dto.setId(res.getIdResenha());
        dto.setEstrellas(res.getCalificacion());
        dto.setComentario(res.getComentario() != null ? res.getComentario() : "");
        dto.setFechaPublicacion(formatearFechaIso(res.getFechaPublicacion()));
        dto.setActivo(res.isActivo());
        String tipoAutor = res.getTipoAutor() != null ? res.getTipoAutor() : "INVITADO";
        dto.setTipoAutor(tipoAutor);
        dto.setObjetivo(res.getObjetivo() != null ? res.getObjetivo() : "ALOJAMIENTO");

        int reservaId = res.getReserva() != null ? res.getReserva().getIdReserva() : 0;
        dto.setReservaId(reservaId);

        // Reserva completa (invitado + alojamiento) vía lookup cacheado.
        Reserva reserva = buscarReserva(reservaId, reservaBL, reservaCache);
        int alojamientoId = reserva != null && reserva.getAlojamiento() != null
                ? reserva.getAlojamiento().getIdAlojamiento() : 0;
        dto.setAlojamientoId(alojamientoId);

        // Reusa el MISMO lookup cacheado (buscarAlojamiento/aloCache) que ya emplea
        // resolverAutorNombre para ANFITRION: aquí poblamos la caché y aprovechamos el
        // objeto Alojamiento para el nombre. No hay lookup extra (la resolución del autor
        // lo recupera de la caché). null si no se pudo resolver -> el frontend hace fallback.
        Alojamiento alojamiento = buscarAlojamiento(alojamientoId, alojamientoBL, aloCache);
        dto.setAlojamientoNombre(alojamiento != null ? alojamiento.getNombre() : null);

        // RF19: anfitrión = dueño del alojamiento de la reserva. Para las reseñas objetivo=ANFITRION
        // (el huésped califica a la persona) el frontend lo usa para agrupar/promediar por anfitrión.
        int anfitrionId = alojamiento != null && alojamiento.getDuenho() != null
                ? alojamiento.getDuenho().getIdUsuario() : 0;
        dto.setAnfitrionId(anfitrionId);
        dto.setAnfitrionNombre(resolverNombreUsuario(anfitrionId, usuarioBL, usuarioCache));

        dto.setAutorNombre(resolverAutorNombre(tipoAutor, reserva, alojamientoId,
                alojamientoBL, usuarioBL, aloCache, usuarioCache));

        // Id del autor: INVITADO -> invitado de la reserva; ANFITRION -> dueño del alojamiento.
        // Mismo origen que el nombre del autor; lo usa el frontend para "editar/eliminar mi reseña".
        int autorId = 0;
        if ("ANFITRION".equalsIgnoreCase(tipoAutor)) {
            autorId = (alojamiento != null && alojamiento.getDuenho() != null)
                    ? alojamiento.getDuenho().getIdUsuario() : 0;
        } else if (reserva != null && reserva.getInvitado() != null) {
            autorId = reserva.getInvitado().getIdUsuario();
        }
        dto.setAutorId(autorId);

        return dto;
    }

    /** Formatea a ISO "yyyy-MM-dd"; null -> null (el frontend aplica el formato bonito). */
    private static String formatearFechaIso(Date fecha) {
        if (fecha == null) {
            return null;
        }
        return new SimpleDateFormat(PATRON_ISO).format(fecha);
    }

    /** Parsea ISO "yyyy-MM-dd" a Date; vacío/null/inválido -> null. */
    private static Date parsearFechaIso(String iso) {
        if (iso == null || iso.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(PATRON_ISO);
            sdf.setLenient(false);
            return sdf.parse(iso.trim());
        } catch (ParseException ex) {
            return null;
        }
    }

    private static Reserva buscarReserva(int reservaId, ReservaBL reservaBL,
                                         Map<Integer, Reserva> reservaCache) {
        if (reservaBL == null || reservaId <= 0) {
            return null;
        }
        if (reservaCache != null && reservaCache.containsKey(reservaId)) {
            return reservaCache.get(reservaId);
        }
        Reserva reserva = null;
        try {
            reserva = reservaBL.obtenerPorId(reservaId);
        } catch (RuntimeException ex) {
            reserva = null; // sin BD / id inexistente -> el frontend tolera vacío
        }
        if (reservaCache != null) {
            reservaCache.put(reservaId, reserva);
        }
        return reserva;
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
            alo = null;
        }
        if (aloCache != null) {
            aloCache.put(alojamientoId, alo);
        }
        return alo;
    }

    /**
     * autorNombre según el tipo de autor:
     *   INVITADO  -> invitado de la reserva (reserva.invitado.idUsuario -> UsuarioBL)
     *   ANFITRION -> dueño del alojamiento (alojamiento.duenho.idUsuario -> UsuarioBL)
     * Cualquier fallo -> "".
     */
    private static String resolverAutorNombre(String tipoAutor, Reserva reserva, int alojamientoId,
                                              AlojamientoBL alojamientoBL, UsuarioBL usuarioBL,
                                              Map<Integer, Alojamiento> aloCache,
                                              Map<Integer, String> usuarioCache) {
        if (usuarioBL == null) {
            return "";
        }
        try {
            int usuarioId = 0;
            if ("ANFITRION".equalsIgnoreCase(tipoAutor)) {
                Alojamiento alo = buscarAlojamiento(alojamientoId, alojamientoBL, aloCache);
                if (alo != null && alo.getDuenho() != null) {
                    usuarioId = alo.getDuenho().getIdUsuario();
                }
            } else { // INVITADO (default)
                if (reserva != null && reserva.getInvitado() != null) {
                    usuarioId = reserva.getInvitado().getIdUsuario();
                }
            }
            return resolverNombreUsuario(usuarioId, usuarioBL, usuarioCache);
        } catch (RuntimeException ex) {
            return "";
        }
    }

    private static String resolverNombreUsuario(int usuarioId, UsuarioBL usuarioBL,
                                                Map<Integer, String> usuarioCache) {
        if (usuarioBL == null || usuarioId <= 0) {
            return "";
        }
        if (usuarioCache != null && usuarioCache.containsKey(usuarioId)) {
            return usuarioCache.get(usuarioId);
        }
        String nombre = "";
        try {
            Usuario u = usuarioBL.obtenerPorId(usuarioId);
            if (u != null) {
                String n = u.getNombre() != null ? u.getNombre() : "";
                String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
                nombre = (n + " " + ap).trim();
            }
        } catch (RuntimeException ex) {
            nombre = "";
        }
        if (usuarioCache != null) {
            usuarioCache.put(usuarioId, nombre);
        }
        return nombre;
    }

    // ============================================================
    // DTO -> Entidad (POST / PUT)
    // ============================================================

    /**
     * Mapea el DTO a una entidad Resenha con la reserva creada SOLO por id
     * (igual que hace el DAO): new Reserva con idReserva = reservaId.
     *
     * fechaPublicacion: el DTO la transporta como ISO "yyyy-MM-dd", así que se re-parsea
     * y se conserva (un PUT ya NO pisa la fecha original). Solo si viene vacía/null/ inválida
     * se usa now() como fallback al crear (el DAO la exige no-nula al guardar).
     */
    public static Resenha toEntity(ResenaDTO dto) {
        if (dto == null) {
            return null;
        }

        Resenha res = new Resenha();
        res.setIdResenha(dto.getId());
        res.setCalificacion(dto.getEstrellas());
        res.setComentario(dto.getComentario());
        Date fecha = parsearFechaIso(dto.getFechaPublicacion());
        res.setFechaPublicacion(fecha != null ? fecha : new Date()); // fallback now() solo si no viene
        res.setTipoAutor(dto.getTipoAutor() != null ? dto.getTipoAutor() : "INVITADO");
        res.setObjetivo(dto.getObjetivo() != null ? dto.getObjetivo() : "ALOJAMIENTO");
        res.setActivo(true);

        Reserva reserva = new Reserva();
        reserva.setIdReserva(dto.getReservaId());
        res.setReserva(reserva);

        return res;
    }
}
