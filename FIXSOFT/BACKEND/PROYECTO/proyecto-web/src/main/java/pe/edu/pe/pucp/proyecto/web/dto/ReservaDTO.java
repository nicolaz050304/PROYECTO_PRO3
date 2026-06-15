package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Reserva. Único objeto expuesto por los endpoints: sin entidades
 * del modelo, sin objetos anidados -> evita los ciclos de serialización
 * (Reserva<->Invitado, Reserva->Alojamiento->Anfitrion->propiedades).
 *
 * Campos camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase case-insensitive. fechaEntrada/fechaSalida viajan como String ISO
 * "yyyy-MM-dd" (sin hora ni 'Z'); el C# las deserializa a DateTime sin líos.
 */
public class ReservaDTO {

    private int id;
    private String alojamientoNombre;
    private String ubicacion;
    private String fechaEntrada;
    private String fechaSalida;
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

    public String getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(String fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }

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
