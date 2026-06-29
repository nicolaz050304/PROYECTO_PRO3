package pe.edu.pe.pucp.proyecto.incidencia.bl;

import pe.edu.pe.pucp.proyecto.incidencia.Incidencia;

import java.util.List;

/** Lógica de negocio de incidencias / soporte. */
public interface IncidenciaBL {

    /** Todas las incidencias para el panel del admin. */
    List<Incidencia> listarTodas();

    /** Valida y registra una incidencia (estado inicial ABIERTO); devuelve su id. */
    int agregar(Incidencia incidencia);

    /** Cambia el estado (ABIERTO / EN_PROCESO / RESUELTO / CERRADO). */
    void cambiarEstado(int idIncidencia, String estado);
}
