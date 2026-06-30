package pe.edu.pe.pucp.proyecto.auditoria.dao;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;

import java.util.List;

/** DAO de la bitácora de transiciones de estado (RNF09). */
public interface AuditoriaEstadoDAO {

    /** Inserta un registro de auditoría; devuelve su id (0 si no se pudo). */
    int agregar(AuditoriaEstado registro);

    /** Historial de una entidad concreta (RESERVA/USUARIO + id), del más reciente al más antiguo. */
    List<AuditoriaEstado> listarPorEntidad(String entidad, int idEntidad);

    /** Últimos N registros (vista global del admin), del más reciente al más antiguo. */
    List<AuditoriaEstado> listarRecientes(int limite);
}
