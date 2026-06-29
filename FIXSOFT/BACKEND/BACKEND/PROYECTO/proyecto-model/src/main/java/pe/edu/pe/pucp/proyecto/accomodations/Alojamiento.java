package pe.edu.pe.pucp.proyecto.accomodations;

import pe.edu.pe.pucp.proyecto.interfaceConsults.IConsultable;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
import pe.edu.pe.pucp.proyecto.users.Usuario; // Para el admin validador

import java.util.ArrayList;
import java.util.List;

public abstract class Alojamiento implements IConsultable {
    private int idAlojamiento;
    private String nombre;
    private String descripcion;
    private double precioPorNoche;
    private String direccion;
    private int capacidadMax;
    private double calificacionPromedio;
    private boolean disponibilidad;
    private Anfitrion duenho;
    private String pais;
    private List<Resenha> resenhasDeClientes;

    // ============================================================
    // NUEVOS CAMPOS (Sincronizados con el SQL de 8 tablas)
    // ============================================================
    private double latitud;         // Para Google Maps (RF10)
    private double longitud;        // Para Google Maps (RF10)
    private String estadoValidacion; // PENDIENTE, APROBADO, RECHAZADO
    private Usuario adminValidador;  // Quién autorizó el alojamiento
    private String imagenUrl;        // URL de la imagen (columna imagen_url)

    // Servicios/amenities y reglas de la casa (columnas servicios/reglas como CSV). Default vacío.
    private List<String> servicios = new ArrayList<>();
    private List<String> reglas = new ArrayList<>();

    public Alojamiento() {
        this.estadoValidacion = "PENDIENTE"; // Valor por defecto
    }

    public Alojamiento(String nombre, String descripcion, double precioPorNoche, String direccion,
                       int capacidadMax, double calificacionPromedio, boolean disponibilidad,
                       Anfitrion duenho, String pais) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioPorNoche = precioPorNoche;
        this.direccion = direccion;
        this.capacidadMax = capacidadMax;
        this.calificacionPromedio = calificacionPromedio;
        this.disponibilidad = disponibilidad;
        this.duenho = duenho;
        this.pais = pais;
        this.estadoValidacion = "PENDIENTE";
    }

    // --- NUEVOS GETTERS Y SETTERS ---
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public Usuario getAdminValidador() { return adminValidador; }
    public void setAdminValidador(Usuario adminValidador) { this.adminValidador = adminValidador; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public List<String> getServicios() { return servicios; }
    public void setServicios(List<String> servicios) { this.servicios = servicios != null ? servicios : new ArrayList<>(); }

    public List<String> getReglas() { return reglas; }
    public void setReglas(List<String> reglas) { this.reglas = reglas != null ? reglas : new ArrayList<>(); }

    // --- TUS GETTERS Y SETTERS ORIGINALES ---
    public int getIdAlojamiento() { return idAlojamiento; }
    public void setIdAlojamiento(int idAlojamiento) { this.idAlojamiento = idAlojamiento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(double precioPorNoche) { this.precioPorNoche = precioPorNoche; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(int capacidadMax) { this.capacidadMax = capacidadMax; }

    public double getCalificacionPromedio() { return calificacionPromedio; }
    public void setCalificacionPromedio(double calificacionPromedio) { this.calificacionPromedio = calificacionPromedio; }

    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Anfitrion getDuenho() { return duenho; }
    public void setDuenho(Anfitrion duenho) { this.duenho = duenho; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public List<Resenha> getResenhasDeClientes() { return resenhasDeClientes; }
    public void setResenhasDeClientes(List<Resenha> resenhasDeClientes) { this.resenhasDeClientes = resenhasDeClientes; }

    // --- MÉTODOS DE NEGOCIO ---
    public void actualizarPrecio(double nuevoPrecioPorNoche) {
        this.precioPorNoche = nuevoPrecioPorNoche;
    }

    public boolean verificarDisponibilidad() {
        return disponibilidad && "APROBADO".equals(estadoValidacion);
    }

    @Override
    public void consultarDatos() {
        System.out.println("Alojamiento: " + nombre + " [" + estadoValidacion + "]");
        System.out.println("Ubicación: " + latitud + ", " + longitud);
    }
}