package pe.edu.pe.pucp.proyecto.validacion.bl;

import pe.edu.pe.pucp.proyecto.validacion.DocumentoValidacion;

import java.util.List;

/** Lógica de negocio de la validación documentaria (RF02). */
public interface DocumentoValidacionBL {

    /** Documento del usuario (la última subida) o null si no ha subido ninguno. */
    DocumentoValidacion obtenerPorUsuario(int idUsuario);

    /** Documento por id, o null si no existe. */
    DocumentoValidacion obtenerPorId(int idDocumento);

    /** Documentos PENDIENTE para la cola de revisión del admin. */
    List<DocumentoValidacion> listarPendientes();

    /** Valida y guarda el documento subido por el usuario (queda PENDIENTE); devuelve su id. */
    int subir(DocumentoValidacion documento);

    /** El admin aprueba o rechaza (solo APROBADO / RECHAZADO); RECHAZADO admite motivo. */
    void decidir(int idDocumento, String estado, String motivoRechazo, int idAdminValidador);
}
