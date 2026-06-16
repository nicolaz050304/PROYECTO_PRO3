package pe.edu.pe.pucp.proyecto.web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO plano de Usuario. Es el ÚNICO objeto expuesto en las respuestas: sin
 * entidades del modelo, sin objetos anidados y, SOBRE TODO, SIN password
 * (la contraseña nunca viaja en una respuesta).
 *
 * Campos en camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase de forma case-insensitive.
 */
public class UsuarioDTO {

    private int id;
    private String correo;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String pais;
    // Rol principal derivado: "ADMINISTRADOR" / "ANFITRION" / "INVITADO".
    private String tipoUsuario;
    // Todos los roles del usuario (el modelo usa un Set<String>).
    private List<String> roles = new ArrayList<>();

    public UsuarioDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
