package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Cuerpo de entrada EXCLUSIVO del endpoint POST UsuarioRS/login.
 * Se mantiene separado de UsuarioDTO a propósito: así el password solo existe
 * en la ENTRADA del login y nunca puede aparecer en una respuesta.
 */
public class LoginRequest {

    private String correo;
    private String password;

    public LoginRequest() {
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
