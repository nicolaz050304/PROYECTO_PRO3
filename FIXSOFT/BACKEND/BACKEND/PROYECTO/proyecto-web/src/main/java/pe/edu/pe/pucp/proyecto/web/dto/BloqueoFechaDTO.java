package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano de un bloqueo de fechas del anfitrión (RF30).
 * Fechas como String ISO "yyyy-MM-dd" (sin hora ni 'Z'), igual que el resto de fechas
 * expuestas por la API y cómodas para flatpickr.
 */
public class BloqueoFechaDTO {

    private int id;
    private int idAlojamiento;
    private String fechaInicio;
    private String fechaFin;
    private String motivo;

    public BloqueoFechaDTO() {
    }

    public BloqueoFechaDTO(int id, int idAlojamiento, String fechaInicio, String fechaFin, String motivo) {
        this.id = id;
        this.idAlojamiento = idAlojamiento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdAlojamiento() { return idAlojamiento; }
    public void setIdAlojamiento(int idAlojamiento) { this.idAlojamiento = idAlojamiento; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
