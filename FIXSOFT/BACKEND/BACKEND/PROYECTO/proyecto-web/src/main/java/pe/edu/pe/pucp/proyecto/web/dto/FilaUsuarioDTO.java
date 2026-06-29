package pe.edu.pe.pucp.proyecto.web.dto;

/** Fila del reporte de Usuarios (RF25): directorio por rol y estado de cuenta. */
public class FilaUsuarioDTO {

    private String nombre;
    private String correo;
    private String rol;
    private String estado;

    public FilaUsuarioDTO() { }

    public FilaUsuarioDTO(String nombre, String correo, String rol, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
