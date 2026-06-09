package pe.edu.pe.pucp.proyecto.users;

import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;
import java.util.Date;

public class Administrador extends Usuario {

    private int nivelAcceso;
    private Date fechaContratacion;
    private String areaResponsabilidad;

    public Administrador() {
        super();
        this.agregarRol("ADMINISTRADOR"); // Asignamos el rol automáticamente
    }

    // MODIFICADO: Añadimos TipoDocumento y telefono a los parámetros
    public Administrador(int idUsuario, String username, String correo, String password, String nombre,
                         String apellidoPaterno, String apellidoMaterno, String pais,
                         TipoDocumento tipoDocumento, String numeroDocumento, String telefono,
                         int nivelAcceso, Date fechaContratacion, String areaResponsabilidad,
                         boolean estadoSesion, EstadoUsuario estadoActual) {

        // MODIFICADO: Pasamos los 11 parámetros exactos que pide el padre (Usuario)
        super(idUsuario, username, correo, password, nombre, apellidoPaterno, apellidoMaterno,
                pais, tipoDocumento, numeroDocumento, telefono, estadoSesion, estadoActual);

        this.nivelAcceso = nivelAcceso;
        this.fechaContratacion = fechaContratacion;
        this.areaResponsabilidad = areaResponsabilidad;

        // Regla de negocio: Un admin suele crearse ya disponible
        this.setEstadoActual(EstadoUsuario.DISPONIBLE);
        this.agregarRol("ADMINISTRADOR");
    }

    // --- GETTERS Y SETTERS ---
    public int getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(int nivelAcceso) { this.nivelAcceso = nivelAcceso; }

    public Date getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public String getAreaResponsabilidad() { return areaResponsabilidad; }
    public void setAreaResponsabilidad(String areaResponsabilidad) { this.areaResponsabilidad = areaResponsabilidad; }


    /**
     * Valida documentos y registra quién fue el administrador validador (Auditoría)
     */
    public boolean validarDocumentos(Usuario usu, String nroDoc) {
        if (nroDoc != null && !nroDoc.isEmpty()) {
            System.out.println("Validando documentos del usuario: " + usu.getNombre());

            // Seteamos los campos de auditoría que agregamos al SQL y al modelo Usuario
            usu.setEstadoValidacion("APROBADO");
            usu.setAdminValidador(this); // Este administrador es el que valida
            usu.setEstadoActual(EstadoUsuario.DISPONIBLE);

            return true;
        }
        usu.setEstadoValidacion("RECHAZADO");
        return false;
    }

    /**
     * Aplica eliminación lógica al usuario (Nota del JP)
     */
    public void bloquearUsuario(Usuario usu) {
        usu.setEstadoActual(EstadoUsuario.SUSPENDIDO);
        usu.setEstadoSesion(false);
        System.out.println("El Administrador " + this.getNombre() + " ha bloqueado a " + usu.getUsername());
    }
}