package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Alojamiento. Es el ÚNICO objeto expuesto por los endpoints:
 * sin entidades del modelo, sin objetos anidados, sin password -> no hay
 * ciclos de serialización (duenho<->propiedades, reserva<->alojamiento<->resenha).
 *
 * Campos en camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase de forma case-insensitive.
 */
public class AlojamientoDTO {

    private int id;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String ubicacion;
    private double precioNoche;
    private int maxHuespedes;
    private int habitaciones;
    private double rating;
    private int totalResenas;
    private String estadoPublicacion;
    private String anfitrionNombre;
    private int anfitrionId;
    private double latitud;
    private double longitud;

    public AlojamientoDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public int getMaxHuespedes() { return maxHuespedes; }
    public void setMaxHuespedes(int maxHuespedes) { this.maxHuespedes = maxHuespedes; }

    public int getHabitaciones() { return habitaciones; }
    public void setHabitaciones(int habitaciones) { this.habitaciones = habitaciones; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalResenas() { return totalResenas; }
    public void setTotalResenas(int totalResenas) { this.totalResenas = totalResenas; }

    public String getEstadoPublicacion() { return estadoPublicacion; }
    public void setEstadoPublicacion(String estadoPublicacion) { this.estadoPublicacion = estadoPublicacion; }

    public String getAnfitrionNombre() { return anfitrionNombre; }
    public void setAnfitrionNombre(String anfitrionNombre) { this.anfitrionNombre = anfitrionNombre; }

    public int getAnfitrionId() { return anfitrionId; }
    public void setAnfitrionId(int anfitrionId) { this.anfitrionId = anfitrionId; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}
