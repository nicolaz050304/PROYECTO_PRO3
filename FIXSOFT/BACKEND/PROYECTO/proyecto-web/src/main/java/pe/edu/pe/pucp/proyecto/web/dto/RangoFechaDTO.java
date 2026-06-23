package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO mínimo para bloquear el calendario del frontend: solo transporta el rango
 * de una reserva ocupada (fechaInicio–fechaFin). A propósito NO incluye datos del
 * huésped (id_invitado, monto, etc.): para pintar las fechas ocupadas basta el rango,
 * y así no se exponen datos sensibles de las reservas de otros usuarios.
 *
 * Fechas como String ISO "yyyy-MM-dd" (sin hora ni 'Z'), fáciles de consumir por flatpickr.
 */
public class RangoFechaDTO {

    private String fechaInicio;
    private String fechaFin;

    public RangoFechaDTO() {
    }

    public RangoFechaDTO(String fechaInicio, String fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
}
