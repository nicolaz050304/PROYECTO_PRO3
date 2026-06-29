package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.Casa;
import pe.edu.pe.pucp.proyecto.accomodations.Departamento;
import pe.edu.pe.pucp.proyecto.accomodations.Habitacion;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.web.dto.AlojamientoDTO;

import java.util.Map;

/**
 * Conversión Alojamiento (entidad del modelo) <-> AlojamientoDTO (contrato plano).
 * Tolera null en todos los campos. La subclase ES el tipo (no hay campo "tipo"
 * en el modelo), así que el tipo se deriva por instanceof.
 */
public final class AlojamientoMapper {

    private AlojamientoMapper() {
    }

    // ============================================================
    // Entidad -> DTO
    // ============================================================

    /** Variante simple: sin lookup de nombre de anfitrión (queda ""). */
    public static AlojamientoDTO toDTO(Alojamiento al) {
        return toDTO(al, null, null);
    }

    /**
     * Mapeo completo. Si se pasa {@code usuarioBL}, se resuelve el nombre real del
     * anfitrión (el DAO solo carga su id). {@code nombreCache} evita repetir el
     * lookup dentro de un mismo request (N+1) — puede ser null.
     */
    public static AlojamientoDTO toDTO(Alojamiento al, UsuarioBL usuarioBL, Map<Integer, String> nombreCache) {
        if (al == null) {
            return null;
        }

        AlojamientoDTO dto = new AlojamientoDTO();
        dto.setId(al.getIdAlojamiento());
        dto.setNombre(al.getNombre());
        dto.setDescripcion(al.getDescripcion());
        dto.setTipo(resolverTipo(al));
        dto.setUbicacion(al.getDireccion());
        dto.setPrecioNoche(al.getPrecioPorNoche());
        dto.setMaxHuespedes(al.getCapacidadMax());
        dto.setHabitaciones(resolverHabitaciones(al));
        dto.setRating(al.getCalificacionPromedio());
        dto.setTotalResenas(al.getResenhasDeClientes() != null ? al.getResenhasDeClientes().size() : 0);
        dto.setEstadoPublicacion(al.isDisponibilidad() ? "Activo" : "Pausado");
        dto.setEstadoValidacion(al.getEstadoValidacion()); // moderación: PENDIENTE/APROBADO/RECHAZADO

        int anfitrionId = al.getDuenho() != null ? al.getDuenho().getIdUsuario() : 0;
        dto.setAnfitrionId(anfitrionId);
        dto.setAnfitrionNombre(resolverNombre(anfitrionId, usuarioBL, nombreCache));

        dto.setLatitud(al.getLatitud());
        dto.setLongitud(al.getLongitud());
        dto.setImagenUrl(al.getImagenUrl());
        dto.setServicios(al.getServicios());
        dto.setReglas(al.getReglas());

        // Atributos específicos por tipo (RF05): cada subtipo expone los suyos en el DTO.
        mapearEspecificos(al, dto);
        return dto;
    }

    /**
     * Vuelca los atributos ESPECÍFICOS de cada subtipo al DTO (RF05). Se usa una sola cadena
     * instanceof para no repetir la detección de tipo; cada alojamiento solo llena los suyos.
     */
    private static void mapearEspecificos(Alojamiento al, AlojamientoDTO dto) {
        if (al instanceof Casa c) {
            dto.setNumPisos(c.getNumPisos());
            dto.setConPatio(c.isConPatio());
            dto.setNumCocheras(c.getNumCocheras());
            dto.setNumHabitacionesCasa(c.getNumHabitaciones());
        } else if (al instanceof Departamento d) {
            dto.setNumPiso(d.getNumPiso());
            dto.setNroDepartamento(d.getNroDepartamento());
            dto.setNroHabitacionesDepartamento(d.getNroHabitaciones());
        } else if (al instanceof Habitacion h) {
            dto.setNroHabitacion(h.getNroHabitacion());
            dto.setTipoCama(h.getTipoCama());
            dto.setConBanhoPrivado(h.isConbanhoPrivado());
        }
    }

    /**
     * Overload anti-N+1: reusa el toDTO de 3 args y SOBRESCRIBE rating/totalResenas
     * SIEMPRE desde {@code resenasCache} (idAlojamiento -> [promedio, total]). Si el
     * alojamiento no tiene reseñas (no está en el cache) quedan en 0 — nunca se usa
     * el valor sembrado en la entidad.
     */
    public static AlojamientoDTO toDTO(Alojamiento al, UsuarioBL usuarioBL,
                                       java.util.Map<Integer, String> nombreCache,
                                       java.util.Map<Integer, double[]> resenasCache) {
        AlojamientoDTO dto = toDTO(al, usuarioBL, nombreCache);
        if (dto != null && al != null && resenasCache != null) {
            double[] resumen = resenasCache.get(al.getIdAlojamiento());
            dto.setRating(resumen != null ? resumen[0] : 0.0);
            dto.setTotalResenas(resumen != null ? (int) resumen[1] : 0);
        }
        return dto;
    }

    /** Casa -> "Casa", Departamento -> "Departamento", Habitacion -> "Habitación" (con tilde). */
    private static String resolverTipo(Alojamiento al) {
        if (al instanceof Casa) {
            return "Casa";
        } else if (al instanceof Departamento) {
            return "Departamento";
        } else if (al instanceof Habitacion) {
            return "Habitación";
        }
        return "";
    }

    private static int resolverHabitaciones(Alojamiento al) {
        if (al instanceof Casa c) {
            return c.getNumHabitaciones();
        } else if (al instanceof Departamento d) {
            return d.getNroHabitaciones();
        } else if (al instanceof Habitacion) {
            return 1;
        }
        return 0;
    }

    private static String resolverNombre(int anfitrionId, UsuarioBL usuarioBL, Map<Integer, String> nombreCache) {
        if (usuarioBL == null || anfitrionId <= 0) {
            return "";
        }
        if (nombreCache != null && nombreCache.containsKey(anfitrionId)) {
            return nombreCache.get(anfitrionId);
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
            // Si el lookup falla (sin BD, id inexistente, etc.) el frontend tolera vacío.
            nombre = "";
        }
        if (nombreCache != null) {
            nombreCache.put(anfitrionId, nombre);
        }
        return nombre;
    }

    // ============================================================
    // DTO -> Entidad (POST / PUT)
    // ============================================================

    /**
     * Instancia la subclase según {@code tipo} y setea solo los campos que existen
     * en el backend. Los campos frontend-only sin destino (tarifaLimpieza, servicios,
     * gradiente, etc.) se ignoran a propósito.
     *
     * RF05: el DTO ahora transporta los atributos propios de cada subclase, así que el
     * formulario los puede enviar y se guardan (el DAO ya los persiste). Cada tipo setea
     * SOLO los suyos desde sus campos específicos del DTO.
     */
    public static Alojamiento toEntity(AlojamientoDTO dto) {
        if (dto == null) {
            return null;
        }

        String tipo = dto.getTipo() != null ? dto.getTipo().trim().toLowerCase() : "";
        Alojamiento al;
        switch (tipo) {
            case "casa" -> {
                Casa c = new Casa();
                c.setNumPisos(dto.getNumPisos());
                c.setConPatio(dto.isConPatio());
                c.setNumCocheras(dto.getNumCocheras());
                c.setNumHabitaciones(dto.getNumHabitacionesCasa());
                al = c;
            }
            case "habitación", "habitacion" -> {
                Habitacion h = new Habitacion();
                h.setNroHabitacion(dto.getNroHabitacion());
                h.setTipoCama(dto.getTipoCama());
                h.setConbanhoPrivado(dto.isConBanhoPrivado());
                al = h;
            }
            case "departamento" -> {
                Departamento d = new Departamento();
                d.setNumPiso(dto.getNumPiso());
                d.setNroDepartamento(dto.getNroDepartamento());
                d.setNroHabitaciones(dto.getNroHabitacionesDepartamento());
                al = d;
            }
            default -> {
                // Fallback razonable: Departamento (el tipo más común del catálogo).
                Departamento d = new Departamento();
                d.setNumPiso(dto.getNumPiso());
                d.setNroDepartamento(dto.getNroDepartamento());
                d.setNroHabitaciones(dto.getNroHabitacionesDepartamento());
                al = d;
            }
        }

        al.setIdAlojamiento(dto.getId());
        al.setNombre(dto.getNombre());
        al.setDescripcion(dto.getDescripcion());
        al.setDireccion(dto.getUbicacion());
        al.setPrecioPorNoche(dto.getPrecioNoche());
        al.setCapacidadMax(dto.getMaxHuespedes());
        al.setCalificacionPromedio(dto.getRating());
        al.setDisponibilidad(!"Pausado".equalsIgnoreCase(dto.getEstadoPublicacion()));
        al.setLatitud(dto.getLatitud());
        al.setLongitud(dto.getLongitud());
        al.setImagenUrl(dto.getImagenUrl());
        al.setServicios(dto.getServicios());
        al.setReglas(dto.getReglas());

        // pais es NOT NULL en BD: ESTE era el campo que faltaba y disparaba el 500.
        // Default "Perú" si el DTO no lo trae (proyecto Perú; todos los sembrados son Perú).
        String pais = dto.getPais() != null && !dto.getPais().isBlank() ? dto.getPais() : "Perú";
        al.setPais(pais);

        // estadoValidacion: el DTO no lo transporta; el constructor ya pone "PENDIENTE",
        // pero lo fijamos explícito por si acaso (default seguro para la moderación).
        al.setEstadoValidacion("PENDIENTE");

        // El DAO inserta/actualiza usando duenho.getIdUsuario().
        Anfitrion duenho = new Anfitrion();
        duenho.setIdUsuario(dto.getAnfitrionId());
        al.setDuenho(duenho);

        return al;
    }
}
