package pe.edu.pe.pucp.proyecto.incidencia;

import java.util.Date;

/**
 * Incidencia / ticket de soporte (RF28 — gestión de incidencias). Un usuario reporta un
 * problema (asunto + descripción + prioridad) y el admin lo gestiona cambiando su estado.
 *
 * Estado: "ABIERTO" (inicial) | "EN_PROCESO" | "RESUELTO" | "CERRADO".
 * Prioridad: "ALTA" | "MEDIA" | "BAJA".
 */
public class Incidencia {

    public static final String ABIERTO = "ABIERTO";
    public static final String EN_PROCESO = "EN_PROCESO";
    public static final String RESUELTO = "RESUELTO";
    public static final String CERRADO = "CERRADO";

    private int idIncidencia;
    private int idUsuario;
    private String asunto;
    private String descripcion;
    private String prioridad;
    private String estado;
    private Date fecha;

    public Incidencia() {
    }

    public Incidencia(int idIncidencia, int idUsuario, String asunto, String descripcion,
                      String prioridad, String estado, Date fecha) {
        this.idIncidencia = idIncidencia;
        this.idUsuario = idUsuario;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(int idIncidencia) { this.idIncidencia = idIncidencia; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
