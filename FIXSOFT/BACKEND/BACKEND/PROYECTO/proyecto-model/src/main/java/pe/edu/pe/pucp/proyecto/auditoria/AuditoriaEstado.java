package pe.edu.pe.pucp.proyecto.auditoria;

import java.util.Date;

/**
 * Registro de auditoría de una transición de estado (RNF09 — auditabilidad). Cada vez que cambia
 * el estado de una RESERVA o de un USUARIO (perfil) se guarda una fila cronológica con marca de
 * tiempo persistente, para que toda transición sea rastreable.
 *
 * entidad : "RESERVA" | "USUARIO"
 * campo   : qué cambió, p. ej. "estado" (reserva), "estado_validacion" / "estado_actual" (usuario)
 */
public class AuditoriaEstado {

    public static final String ENTIDAD_RESERVA = "RESERVA";
    public static final String ENTIDAD_USUARIO = "USUARIO";

    private int idAuditoria;
    private String entidad;
    private int idEntidad;
    private String campo;
    private String estadoAnterior;   // puede ser null (creación / primer estado)
    private String estadoNuevo;
    private String detalle;          // contexto opcional: "por admin #1", motivo de rechazo, etc.
    private Date fecha;              // la pone la BD (DEFAULT CURRENT_TIMESTAMP)

    public AuditoriaEstado() {
    }

    public AuditoriaEstado(int idAuditoria, String entidad, int idEntidad, String campo,
                           String estadoAnterior, String estadoNuevo, String detalle, Date fecha) {
        this.idAuditoria = idAuditoria;
        this.entidad = entidad;
        this.idEntidad = idEntidad;
        this.campo = campo;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public int getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(int idAuditoria) { this.idAuditoria = idAuditoria; }

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

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
