package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano del documento de validación (RF02).
 * - Subida (POST): el cliente envía usuarioId, tipoDocumento, numeroDocumento, archivoBase64.
 * - Lectura (GET): viaja completo, con el nombre/correo del usuario resueltos para el panel admin.
 * - Decisión (PUT): el cliente envía estado (APROBADO/RECHAZADO), motivoRechazo y adminId.
 * Fechas como String ISO. archivoBase64 es el data URL del documento (imagen/PDF).
 */
public class DocumentoValidacionDTO {

    private int id;
    private int usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;
    private String tipoDocumento;
    private String numeroDocumento;
    private String archivoBase64;
    private String estado;
    private String motivoRechazo;
    private int adminId;
    private String fechaSubida;
    private String fechaRevision;

    public DocumentoValidacionDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getUsuarioCorreo() { return usuarioCorreo; }
    public void setUsuarioCorreo(String usuarioCorreo) { this.usuarioCorreo = usuarioCorreo; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getArchivoBase64() { return archivoBase64; }
    public void setArchivoBase64(String archivoBase64) { this.archivoBase64 = archivoBase64; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(String fechaSubida) { this.fechaSubida = fechaSubida; }

    public String getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(String fechaRevision) { this.fechaRevision = fechaRevision; }
}
