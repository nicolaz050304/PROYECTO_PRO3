package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano de un registro de auditoría (RNF09). 'fecha' viaja como String ISO
 * "yyyy-MM-dd HH:mm:ss" (la marca de tiempo del servidor).
 */
public class AuditoriaEstadoDTO {

    private int id;
    private String entidad;
    private int idEntidad;
    private String campo;
    private String estadoAnterior;
    private String estadoNuevo;
    private String detalle;
    private String fecha;

    public AuditoriaEstadoDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public int getIdEntidad() { return idEntidad; }
    public void setIdEntidad(int idEntidad) { this.idEntidad = idEntidad; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
