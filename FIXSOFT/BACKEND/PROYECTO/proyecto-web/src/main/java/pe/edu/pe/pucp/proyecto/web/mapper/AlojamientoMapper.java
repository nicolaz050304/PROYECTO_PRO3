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

        int anfitrionId = al.getDuenho() != null ? al.getDuenho().getIdUsuario() : 0;
        dto.setAnfitrionId(anfitrionId);
        dto.setAnfitrionNombre(resolverNombre(anfitrionId, usuarioBL, nombreCache));

        dto.setLatitud(al.getLatitud());
        dto.setLongitud(al.getLongitud());
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
     * TODO: el DTO no transporta los atributos propios de cada subclase
     * (numPisos/conPatio/numCocheras de Casa, numPiso/nroDepartamento de Departamento,
     * tipoCama/conBanhoPrivado/nroHabitacion de Habitacion) salvo "habitaciones".
     * Quedan en su valor por defecto. Las escrituras aún no se usan desde el frontend.
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
                c.setNumHabitaciones(dto.getHabitaciones());
                al = c;
            }
            case "habitación", "habitacion" -> al = new Habitacion();
            case "departamento" -> {
                Departamento d = new Departamento();
                d.setNroHabitaciones(dto.getHabitaciones());
                al = d;
            }
            default -> {
                // Fallback razonable: Departamento (el tipo más común del catálogo).
                Departamento d = new Departamento();
                d.setNroHabitaciones(dto.getHabitaciones());
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

        // El DAO inserta/actualiza usando duenho.getIdUsuario().
        Anfitrion duenho = new Anfitrion();
        duenho.setIdUsuario(dto.getAnfitrionId());
        al.setDuenho(duenho);

        return al;
    }
}
