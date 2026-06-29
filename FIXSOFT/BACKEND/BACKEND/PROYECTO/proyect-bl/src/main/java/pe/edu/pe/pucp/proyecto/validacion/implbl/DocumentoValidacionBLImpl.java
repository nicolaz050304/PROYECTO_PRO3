package pe.edu.pe.pucp.proyecto.validacion.implbl;

import pe.edu.pe.pucp.proyecto.validacion.DocumentoValidacion;
import pe.edu.pe.pucp.proyecto.validacion.bl.DocumentoValidacionBL;
import pe.edu.pe.pucp.proyecto.validacion.dao.DocumentoValidacionDAO;
import pe.edu.pe.pucp.proyecto.validacion.impl.DocumentoValidacionImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DocumentoValidacionBLImpl implements DocumentoValidacionBL {

    private final DocumentoValidacionDAO dao = new DocumentoValidacionImpl();

    // Solo el admin puede dejar el documento en estos dos estados finales.
    private static final Set<String> ESTADOS_DECISION =
            Set.of(DocumentoValidacion.APROBADO, DocumentoValidacion.RECHAZADO);

    @Override
    public DocumentoValidacion obtenerPorUsuario(int idUsuario) {
        return dao.buscarPorUsuario(idUsuario);
    }

    @Override
    public DocumentoValidacion obtenerPorId(int idDocumento) {
        return dao.buscarPorId(idDocumento);
    }

    @Override
    public List<DocumentoValidacion> listarPendientes() {
        return dao.listarPendientes();
    }

    @Override
    public int subir(DocumentoValidacion d) {
        if (d == null) {
            throw new RuntimeException("Error: el documento no puede ser nulo.");
        }
        if (d.getIdUsuario() <= 0) {
            throw new RuntimeException("Error: el usuario es obligatorio.");
        }
        if (d.getTipoDocumento() == null || d.getTipoDocumento().trim().isEmpty()) {
            throw new RuntimeException("Error: el tipo de documento es obligatorio.");
        }
        if (d.getNumeroDocumento() == null || d.getNumeroDocumento().trim().isEmpty()) {
            throw new RuntimeException("Error: el número de documento es obligatorio.");
        }
        if (d.getArchivoBase64() == null || d.getArchivoBase64().trim().isEmpty()) {
            throw new RuntimeException("Error: debes adjuntar el documento.");
        }
        // Toda subida nace PENDIENTE y con fecha de hoy (no se confía en el cliente).
        d.setEstado(DocumentoValidacion.PENDIENTE);
        d.setFechaSubida(new Date());
        return dao.guardar(d);
    }

    @Override
    public void decidir(int idDocumento, String estado, String motivoRechazo, int idAdminValidador) {
        if (idDocumento <= 0) {
            throw new RuntimeException("Error: id de documento inválido.");
        }
        if (estado == null || !ESTADOS_DECISION.contains(estado)) {
            throw new RuntimeException("Error: la decisión debe ser APROBADO o RECHAZADO.");
        }
        if (DocumentoValidacion.RECHAZADO.equals(estado)
                && (motivoRechazo == null || motivoRechazo.trim().isEmpty())) {
            throw new RuntimeException("Error: indica el motivo del rechazo.");
        }
        dao.actualizarDecision(idDocumento, estado,
                DocumentoValidacion.RECHAZADO.equals(estado) ? motivoRechazo : null, idAdminValidador);
    }
}
