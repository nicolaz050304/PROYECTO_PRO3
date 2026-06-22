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
    private String pais;
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
    private String imagenUrl;

    // Atributos ESPECÍFICOS por tipo (RF05): el DTO ahora los transporta para que el formulario los
    // pueda enviar y se guarden (el DAO ya los persiste). Cada alojamiento solo usa los de SU tipo;
    // los demás quedan en su valor por defecto (0 / null). Nombres numHabitacionesCasa /
    // nroHabitacionesDepartamento para no chocar con el campo común 'habitaciones'.
    // Casa
    private int numPisos;
    private boolean conPatio;
    private int numCocheras;
    private int numHabitacionesCasa;
    // Departamento
    private int numPiso;
    private String nroDepartamento;
    private int nroHabitacionesDepartamento;
    // Habitación
    private String nroHabitacion;
    private String tipoCama;
    private boolean conBanhoPrivado;

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

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

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

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    // --- Casa ---
    public int getNumPisos() { return numPisos; }
    public void setNumPisos(int numPisos) { this.numPisos = numPisos; }

    public boolean isConPatio() { return conPatio; }
    public void setConPatio(boolean conPatio) { this.conPatio = conPatio; }

    public int getNumCocheras() { return numCocheras; }
    public void setNumCocheras(int numCocheras) { this.numCocheras = numCocheras; }

    public int getNumHabitacionesCasa() { return numHabitacionesCasa; }
    public void setNumHabitacionesCasa(int numHabitacionesCasa) { this.numHabitacionesCasa = numHabitacionesCasa; }

    // --- Departamento ---
    public int getNumPiso() { return numPiso; }
    public void setNumPiso(int numPiso) { this.numPiso = numPiso; }

    public String getNroDepartamento() { return nroDepartamento; }
    public void setNroDepartamento(String nroDepartamento) { this.nroDepartamento = nroDepartamento; }

    public int getNroHabitacionesDepartamento() { return nroHabitacionesDepartamento; }
    public void setNroHabitacionesDepartamento(int nroHabitacionesDepartamento) { this.nroHabitacionesDepartamento = nroHabitacionesDepartamento; }

    // --- Habitación ---
    public String getNroHabitacion() { return nroHabitacion; }
    public void setNroHabitacion(String nroHabitacion) { this.nroHabitacion = nroHabitacion; }

    public String getTipoCama() { return tipoCama; }
    public void setTipoCama(String tipoCama) { this.tipoCama = tipoCama; }

    public boolean isConBanhoPrivado() { return conBanhoPrivado; }
    public void setConBanhoPrivado(boolean conBanhoPrivado) { this.conBanhoPrivado = conBanhoPrivado; }
}
