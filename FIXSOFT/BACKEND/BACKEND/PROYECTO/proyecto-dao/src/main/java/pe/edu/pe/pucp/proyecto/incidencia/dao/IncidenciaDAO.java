package pe.edu.pe.pucp.proyecto.incidencia.dao;

import pe.edu.pe.pucp.proyecto.incidencia.Incidencia;

import java.util.List;

/** DAO de incidencias / tickets de soporte. */
public interface IncidenciaDAO {

    /** Todas las incidencias, de la más reciente a la más antigua (para el admin). */
    List<Incidencia> listarTodas();

    /** Inserta una incidencia y devuelve su id generado (0 si no se pudo). */
    int agregar(Incidencia incidencia);

    /** Cambia el estado de una incidencia. */
    void actualizarEstado(int idIncidencia, String estado);
}
