package pe.edu.pe.pucp.proyecto.validacion.dao;

import pe.edu.pe.pucp.proyecto.validacion.DocumentoValidacion;

import java.util.List;

/** DAO del documento de validación de identidad (RF02). Una fila por usuario. */
public interface DocumentoValidacionDAO {

    /** Documento del usuario (la última subida) o null si no ha subido ninguno. */
    DocumentoValidacion buscarPorUsuario(int idUsuario);

    /** Documento por su id, o null si no existe. */
    DocumentoValidacion buscarPorId(int idDocumento);

    /** Documentos en estado PENDIENTE, de la subida más reciente a la más antigua (para el admin). */
    List<DocumentoValidacion> listarPendientes();

    /**
     * Inserta o REEMPLAZA (upsert por id_usuario) el documento del usuario, dejándolo PENDIENTE.
     * Devuelve el id del documento.
     */
    int guardar(DocumentoValidacion documento);

    /** El admin sella la decisión: estado (APROBADO/RECHAZADO), motivo y el id del admin revisor. */
    void actualizarDecision(int idDocumento, String estado, String motivoRechazo, int idAdminValidador);
}
