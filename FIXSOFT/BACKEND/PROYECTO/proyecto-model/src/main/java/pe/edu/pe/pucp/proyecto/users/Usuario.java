package pe.edu.pe.pucp.proyecto.users;

import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento; // <-- IMPORTANTE: Importamos el Enum
import java.util.HashSet;
import java.util.Set;

public abstract class Usuario {
    private int idUsuario;
    private String username;
    private String correo;
    private String password;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String pais;
    private boolean estadoSesion;
    private EstadoUsuario estadoActual;

    // ============================================================
    // NUEVOS CAMPOS (Añadidos para coincidir con SQL y el DAO)
    // ============================================================
    private TipoDocumento tipoDocumento; // <-- AÑADIDO SÍ O SÍ
    private String numeroDocumento;
    private String telefono;             // <-- AÑADIDO SÍ O SÍ
    private Set<String> roles;
    private String estadoValidacion;
    private Usuario adminValidador;

    public Usuario(){
        // Inicializamos el Set para evitar NullPointerException
        this.roles = new HashSet<>();
        this.estadoValidacion = "PENDIENTE";
    }

    public Usuario(int idUsuario, String username, String correo, String password,
                   String nombre, String apellidoPaterno, String apellidoMaterno,
                   String pais, TipoDocumento tipoDocumento, String numeroDocumento, String telefono, // Añadidos
                   boolean estadoSesion, EstadoUsuario estadoActual) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.correo = correo;
        this.password = password;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.pais = pais;
        this.tipoDocumento = tipoDocumento;     // Asignación
        this.numeroDocumento = numeroDocumento; // Asignación
        this.telefono = telefono;               // Asignación
        this.estadoActual = EstadoUsuario.PENDIENTE_VALIDACION;
        this.estadoSesion = estadoSesion;

        // Inicialización de campos de auditoría
        this.roles = new HashSet<>();
        this.estadoValidacion = "PENDIENTE";
    }

    // --- GETTERS Y SETTERS DE LOS NUEVOS CAMPOS ---
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public String getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public Usuario getAdminValidador() { return adminValidador; }
    public void setAdminValidador(Usuario adminValidador) { this.adminValidador = adminValidador; }

    // Método fundamental para el SET de MySQL
    public void agregarRol(String rol) {
        if(this.roles == null) this.roles = new HashSet<>();
        this.roles.add(rol.toUpperCase());
    }

    // --- TUS GETTERS Y SETTERS ORIGINALES ---
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public boolean isEstadoSesion() { return estadoSesion; }
    public void setEstadoSesion(boolean estadoSesion) { this.estadoSesion = estadoSesion; }

    public EstadoUsuario getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoUsuario estadoActual) { this.estadoActual = estadoActual; }

    // --- TUS MÉTODOS DE NEGOCIO ---
    public boolean iniciarSesion(String userIngresado, String passIngresado) {
        if (estadoActual != EstadoUsuario.DISPONIBLE) {
            System.out.println("ERROR: La cuenta no está activa.");
            return false;
        }
        if (this.username.equals(userIngresado) && this.password.equals(passIngresado)) {
            this.estadoSesion = true;
            System.out.println("Bienvenido, " + this.username);
            return true;
        } else {
            System.out.println("Error: Usuario o contraseña incorrectos.");
            return false;
        }
    }

    public void cerrarSesion() {
        if (estadoSesion) {
            this.estadoSesion = false;
            System.out.println("Cierre de sesion correcto.");
        }
    }

    public boolean cambiarPassword(String passActual, String nuevoPass) {
        if (!this.password.equals(passActual)) {
            System.out.println("Error: La contraseña actual no coincide.");
            return false;
        }
        if (nuevoPass.length() < 8) {
            System.out.println("Error: La nueva contraseña debe tener al menos 8 caracteres.");
            return false;
        }
        this.password = nuevoPass;
        System.out.println("Contraseña actualizada con éxito.");
        return true;
    }

    public void actualizarPerfil(String nuevoNombre, String apePaterno, String apeMaterno, String nuevoCorreo, String nuevoPais, String nuevoUserName) {
        if (nuevoCorreo.contains("@")) {
            this.nombre = nuevoNombre;
            this.apellidoPaterno = apePaterno;
            this.apellidoMaterno = apeMaterno;
            this.correo = nuevoCorreo;
            this.pais = nuevoPais;
            this.username = nuevoUserName;
            System.out.println("Perfil actualizado correctamente.");
        } else {
            System.out.println("Error: Datos de perfil inválidos.");
        }
    }

    public void desactivarCuenta() {
        this.estadoActual = EstadoUsuario.SUSPENDIDO;
        this.estadoSesion = false;
        System.out.println("La cuenta ha sido desactivada lógicamente.");
    }
}