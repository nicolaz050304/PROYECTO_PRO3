package pe.edu.pe.pucp.proyecto.validacion;

import java.util.Date;

/**
 * Documento de identidad subido por un usuario para la validación documentaria (RF02).
 * Hay UNA fila por usuario (la última subida): cuando el usuario reenvía, se reemplaza
 * y vuelve a PENDIENTE. La fuente de verdad de "usuario verificado" es la columna
 * usuario.estado_validacion; esta tabla guarda el archivo y el historial de revisión.
 *
 * Estado: "PENDIENTE" (inicial) | "APROBADO" | "RECHAZADO".
 */
public class DocumentoValidacion {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADO = "APROBADO";
    public static final String RECHAZADO = "RECHAZADO";

    private int idDocumento;
    private int idUsuario;
    private String tipoDocumento;     // DNI / PASAPORTE / CE / RUC / SSN
    private String numeroDocumento;
    private String archivoBase64;     // data URL del DNI/pasaporte (imagen o PDF)
    private String estado;
    private String motivoRechazo;
    private int idAdminValidador;     // 0 = sin revisar todavía
    private Date fechaSubida;
    private Date fechaRevision;

    public DocumentoValidacion() {
    }

    public DocumentoValidacion(int idDocumento, int idUsuario, String tipoDocumento, String numeroDocumento,
                               String archivoBase64, String estado, String motivoRechazo, int idAdminValidador,
                               Date fechaSubida, Date fechaRevision) {
        this.idDocumento = idDocumento;
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.archivoBase64 = archivoBase64;
        this.estado = estado;
        this.motivoRechazo = motivoRechazo;
        this.idAdminValidador = idAdminValidador;
        this.fechaSubida = fechaSubida;
        this.fechaRevision = fechaRevision;
    }

    public int getIdDocumento() { return idDocumento; }
    public void setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

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

    public int getIdAdminValidador() { return idAdminValidador; }
    public void setIdAdminValidador(int idAdminValidador) { this.idAdminValidador = idAdminValidador; }

    public Date getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Date fechaSubida) { this.fechaSubida = fechaSubida; }

    public Date getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(Date fechaRevision) { this.fechaRevision = fechaRevision; }
}
