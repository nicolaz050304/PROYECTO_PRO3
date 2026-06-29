package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Notificación (RF18). Nunca se expone la entidad (su Usuario tiene ciclos):
 * solo el id del usuario y los datos que el frontend necesita para el feed/badge.
 */
public class NotificacionDTO {

    private int idNotificacion;
    private String titulo;
    private String mensaje;
    private boolean leido;
    private int idUsuario;

    public NotificacionDTO() {}

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}
