package pe.edu.pe.pucp.proyecto.notif;

import pe.edu.pe.pucp.proyecto.users.Usuario;

public class Notificaciones {
    private int idNotificacion;
    private String titulo;
    private String mensaje;
    private boolean leido;
    private String categoria = "GENERAL"; // RESERVA / MENSAJE / PAGO / GENERAL (para filtrar el feed)
    private Usuario usuario; // Cambiado de 'destinatario' a 'usuario'

    public Notificaciones() {}

    // Getters y Setters corregidos
    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}