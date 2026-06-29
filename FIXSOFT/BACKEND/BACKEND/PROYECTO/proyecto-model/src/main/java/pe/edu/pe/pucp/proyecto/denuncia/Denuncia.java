package pe.edu.pe.pucp.proyecto.denuncia;

import java.util.Date;

/**
 * Denuncia de un huésped contra un alojamiento (RF31). Registra quién denuncia
 * (denunciante), el alojamiento reportado y, derivado de éste, su anfitrión
 * (el "usuario denunciado" que ve el admin). El admin gestiona el estado.
 *
 * Estado: "EN_REVISION" (inicial) | "RESUELTO" | "CERRADO".
 */
public class Denuncia {

    public static final String EN_REVISION = "EN_REVISION";
    public static final String RESUELTO = "RESUELTO";
    public static final String CERRADO = "CERRADO";

    private int idDenuncia;
    private int idDenunciante;
    private int idAlojamiento;
    private int idAnfitrion;
    private String motivo;
    private String descripcion;
    private String estado;
    private Date fecha;

    public Denuncia() {
    }

    public Denuncia(int idDenuncia, int idDenunciante, int idAlojamiento, int idAnfitrion,
                    String motivo, String descripcion, String estado, Date fecha) {
        this.idDenuncia = idDenuncia;
        this.idDenunciante = idDenunciante;
        this.idAlojamiento = idAlojamiento;
        this.idAnfitrion = idAnfitrion;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdDenuncia() { return idDenuncia; }
    public void setIdDenuncia(int idDenuncia) { this.idDenuncia = idDenuncia; }

    public int getIdDenunciante() { return idDenunciante; }
    public void setIdDenunciante(int idDenunciante) { this.idDenunciante = idDenunciante; }

    public int getIdAlojamiento() { return idAlojamiento; }
    public void setIdAlojamiento(int idAlojamiento) { this.idAlojamiento = idAlojamiento; }

    public int getIdAnfitrion() { return idAnfitrion; }
    public void setIdAnfitrion(int idAnfitrion) { this.idAnfitrion = idAnfitrion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
