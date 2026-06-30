package pe.edu.pe.pucp.proyecto.auditoria.bl;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;

import java.util.List;

/** Lógica de la bitácora de auditoría de transiciones de estado (RNF09). */
public interface AuditoriaEstadoBL {

    /**
     * Registra una transición. Es una operación SECUNDARIA: si falla (p. ej. BD), NO propaga la
     * excepción para no romper la operación de negocio que la disparó (solo la deja registrada en log).
     */
    void registrar(String entidad, int idEntidad, String campo,
                   String estadoAnterior, String estadoNuevo, String detalle);

    /** Historial de una entidad (RESERVA/USUARIO + id), del más reciente al más antiguo. */
    List<AuditoriaEstado> listarPorEntidad(String entidad, int idEntidad);

    /** Últimos N registros para la vista global del admin. */
    List<AuditoriaEstado> listarRecientes(int limite);
}
