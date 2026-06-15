package pe.edu.pe.pucp.proyecto.web.dto;

import java.util.Date;

/**
 * DTO plano de Reserva. Único objeto expuesto por los endpoints: sin entidades
 * del modelo, sin objetos anidados -> evita los ciclos de serialización
 * (Reserva<->Invitado, Reserva->Alojamiento->Anfitrion->propiedades).
 *
 * Campos camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase case-insensitive. fechaEntrada/fechaSalida viajan como fecha ISO
 * y el C# las deserializa a DateTime.
 */
public class ReservaDTO {

    private int id;
    private String alojamientoNombre;
    private String ubicacion;
    private Date fechaEntrada;
    private Date fechaSalida;
    private int numHuespedes;
    private double total;
    private String estado;
    private String anfitrionNombre;
    private int huespedId;
    private int alojamientoId;

    public ReservaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAlojamientoNombre() { return alojamientoNombre; }
    public void setAlojamientoNombre(String alojamientoNombre) { this.alojamientoNombre = alojamientoNombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Date getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(Date fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public Date getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Date fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getNumHuespedes() { return numHuespedes; }
    public void setNumHuespedes(int numHuespedes) { this.numHuespedes = numHuespedes; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getAnfitrionNombre() { return anfitrionNombre; }
    public void setAnfitrionNombre(String anfitrionNombre) { this.anfitrionNombre = anfitrionNombre; }

    public int getHuespedId() { return huespedId; }
    public void setHuespedId(int huespedId) { this.huespedId = huespedId; }

    public int getAlojamientoId() { return alojamientoId; }
    public void setAlojamientoId(int alojamientoId) { this.alojamientoId = alojamientoId; }
}
