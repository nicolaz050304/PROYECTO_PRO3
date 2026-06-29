package pe.edu.pe.pucp.proyecto.incidencia.implbl;

import pe.edu.pe.pucp.proyecto.incidencia.Incidencia;
import pe.edu.pe.pucp.proyecto.incidencia.bl.IncidenciaBL;
import pe.edu.pe.pucp.proyecto.incidencia.dao.IncidenciaDAO;
import pe.edu.pe.pucp.proyecto.incidencia.impl.IncidenciaImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class IncidenciaBLImpl implements IncidenciaBL {

    private final IncidenciaDAO dao = new IncidenciaImpl();

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of(Incidencia.ABIERTO, Incidencia.EN_PROCESO, Incidencia.RESUELTO, Incidencia.CERRADO);
    private static final Set<String> PRIORIDADES_VALIDAS = Set.of("ALTA", "MEDIA", "BAJA");

    @Override
    public List<Incidencia> listarTodas() {
        return dao.listarTodas();
    }

    @Override
    public int agregar(Incidencia inc) {
        if (inc == null) {
            throw new RuntimeException("Error: la incidencia no puede ser nula.");
        }
        if (inc.getIdUsuario() <= 0) {
            throw new RuntimeException("Error: el usuario es obligatorio.");
        }
        if (inc.getAsunto() == null || inc.getAsunto().trim().isEmpty()) {
            throw new RuntimeException("Error: el asunto es obligatorio.");
        }
        // Prioridad: la elige el usuario; si no es válida, MEDIA por defecto.
        String prioridad = inc.getPrioridad() != null ? inc.getPrioridad().trim().toUpperCase() : "MEDIA";
        if (!PRIORIDADES_VALIDAS.contains(prioridad)) prioridad = "MEDIA";
        inc.setPrioridad(prioridad);
        // Toda incidencia nace ABIERTA y con fecha de hoy (no se confía en el cliente).
        inc.setEstado(Incidencia.ABIERTO);
        if (inc.getFecha() == null) {
            inc.setFecha(new Date());
        }
        return dao.agregar(inc);
    }

    @Override
    public void cambiarEstado(int idIncidencia, String estado) {
        if (idIncidencia <= 0) {
            throw new RuntimeException("Error: id de incidencia inválido.");
        }
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new RuntimeException("Error: estado inválido. Use ABIERTO, EN_PROCESO, RESUELTO o CERRADO.");
        }
        dao.actualizarEstado(idIncidencia, estado);
    }
}
