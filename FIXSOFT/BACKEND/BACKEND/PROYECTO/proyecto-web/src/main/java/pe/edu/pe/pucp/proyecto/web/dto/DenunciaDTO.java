package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano de una denuncia (RF31). En la creación (POST) el cliente solo envía
 * denuncianteId, alojamientoId, motivo y descripcion; el resto (anfitrión, nombres,
 * estado, fecha) lo resuelve/asigna el backend. En la lectura (GET) viaja completo.
 * Fecha como String ISO "yyyy-MM-dd".
 */
public class DenunciaDTO {

    private int id;
    private int denuncianteId;
    private String denuncianteNombre;
    private int alojamientoId;
    private String alojamientoNombre;
    private int anfitrionId;
    private String anfitrionNombre;
    private String motivo;
    private String descripcion;
    private String estado;
    private String fecha;

    public DenunciaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDenuncianteId() { return denuncianteId; }
    public void setDenuncianteId(int denuncianteId) { this.denuncianteId = denuncianteId; }

    public String getDenuncianteNombre() { return denuncianteNombre; }
    public void setDenuncianteNombre(String denuncianteNombre) { this.denuncianteNombre = denuncianteNombre; }

    public int getAlojamientoId() { return alojamientoId; }
    public void setAlojamientoId(int alojamientoId) { this.alojamientoId = alojamientoId; }

    public String getAlojamientoNombre() { return alojamientoNombre; }
    public void setAlojamientoNombre(String alojamientoNombre) { this.alojamientoNombre = alojamientoNombre; }

    public int getAnfitrionId() { return anfitrionId; }
    public void setAnfitrionId(int anfitrionId) { this.anfitrionId = anfitrionId; }

    public String getAnfitrionNombre() { return anfitrionNombre; }
    public void setAnfitrionNombre(String anfitrionNombre) { this.anfitrionNombre = anfitrionNombre; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
