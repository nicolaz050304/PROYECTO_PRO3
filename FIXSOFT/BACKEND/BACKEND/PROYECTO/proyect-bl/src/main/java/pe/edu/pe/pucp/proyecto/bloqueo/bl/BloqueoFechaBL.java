package pe.edu.pe.pucp.proyecto.bloqueo.bl;

import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;

import java.util.List;

/** Lógica de negocio de bloqueos de fecha del anfitrión (RF30). */
public interface BloqueoFechaBL {

    /** Bloqueos de un alojamiento (vacío si el id no es válido). */
    List<BloqueoFecha> listarPorAlojamiento(int idAlojamiento);

    /** Valida y registra un bloqueo; devuelve su id generado. */
    int agregar(BloqueoFecha bloqueo);

    /** Elimina un bloqueo por id. */
    void eliminar(int idBloqueo);
}
