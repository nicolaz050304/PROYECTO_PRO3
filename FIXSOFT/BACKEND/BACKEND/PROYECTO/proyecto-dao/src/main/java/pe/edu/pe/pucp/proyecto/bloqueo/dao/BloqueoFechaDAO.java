package pe.edu.pe.pucp.proyecto.bloqueo.dao;

import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;

import java.util.List;

/** DAO de bloqueos de fecha del anfitrión (RF30). */
public interface BloqueoFechaDAO {

    /** Bloqueos vigentes de un alojamiento, ordenados por fecha de inicio. */
    List<BloqueoFecha> listarPorAlojamiento(int idAlojamiento);

    /** Inserta un bloqueo y devuelve su id generado (0 si no se pudo). */
    int agregar(BloqueoFecha bloqueo);

    /** Elimina un bloqueo por su id. */
    void eliminar(int idBloqueo);
}
