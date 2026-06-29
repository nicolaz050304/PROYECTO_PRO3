package pe.edu.pe.pucp.proyecto.bloqueo.implbl;

import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;
import pe.edu.pe.pucp.proyecto.bloqueo.bl.BloqueoFechaBL;
import pe.edu.pe.pucp.proyecto.bloqueo.dao.BloqueoFechaDAO;
import pe.edu.pe.pucp.proyecto.bloqueo.impl.BloqueoFechaImpl;

import java.util.Collections;
import java.util.List;

public class BloqueoFechaBLImpl implements BloqueoFechaBL {

    private final BloqueoFechaDAO dao = new BloqueoFechaImpl();

    @Override
    public List<BloqueoFecha> listarPorAlojamiento(int idAlojamiento) {
        if (idAlojamiento <= 0) return Collections.emptyList();
        return dao.listarPorAlojamiento(idAlojamiento);
    }

    @Override
    public int agregar(BloqueoFecha bloqueo) {
        // --- VALIDACIONES DE NEGOCIO ---
        if (bloqueo == null) {
            throw new RuntimeException("Error: el bloqueo no puede ser nulo.");
        }
        if (bloqueo.getIdAlojamiento() <= 0) {
            throw new RuntimeException("Error: debe indicarse un alojamiento válido.");
        }
        if (bloqueo.getFechaInicio() == null || bloqueo.getFechaFin() == null) {
            throw new RuntimeException("Error: las fechas de inicio y fin son obligatorias.");
        }
        if (bloqueo.getFechaFin().before(bloqueo.getFechaInicio())) {
            throw new RuntimeException("Error: la fecha de fin no puede ser anterior a la de inicio.");
        }
        return dao.agregar(bloqueo);
    }

    @Override
    public void eliminar(int idBloqueo) {
        if (idBloqueo <= 0) return;
        dao.eliminar(idBloqueo);
    }
}
