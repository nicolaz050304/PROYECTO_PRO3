package pe.edu.pe.pucp.proyecto.bloqueo;

import java.util.Date;

/**
 * Bloqueo de fechas de un alojamiento por parte del anfitrión (RF30).
 * Representa un rango [fechaInicio, fechaFin] en el que el anfitrión no quiere
 * recibir reservas (mantenimiento, uso personal, etc.). A efectos del calendario
 * del huésped, estos rangos cuentan igual que una reserva ocupada.
 */
public class BloqueoFecha {

    private int idBloqueo;
    private int idAlojamiento;
    private Date fechaInicio;
    private Date fechaFin;
    private String motivo;

    public BloqueoFecha() {
    }

    public BloqueoFecha(int idBloqueo, int idAlojamiento, Date fechaInicio, Date fechaFin, String motivo) {
        this.idBloqueo = idBloqueo;
        this.idAlojamiento = idAlojamiento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
    }

    public int getIdBloqueo() { return idBloqueo; }
    public void setIdBloqueo(int idBloqueo) { this.idBloqueo = idBloqueo; }

    public int getIdAlojamiento() { return idAlojamiento; }
    public void setIdAlojamiento(int idAlojamiento) { this.idAlojamiento = idAlojamiento; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
