package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano de una incidencia / ticket de soporte. En la creación (POST) el cliente
 * envía usuarioId, asunto, descripcion y prioridad; el backend asigna estado y fecha y
 * resuelve usuarioNombre/usuarioCorreo en la lectura. Fecha como String ISO "yyyy-MM-dd".
 */
public class IncidenciaDTO {

    private int id;
    private int usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;
    private String asunto;
    private String descripcion;
    private String prioridad;
    private String estado;
    private String fecha;

    public IncidenciaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getUsuarioCorreo() { return usuarioCorreo; }
    public void setUsuarioCorreo(String usuarioCorreo) { this.usuarioCorreo = usuarioCorreo; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
