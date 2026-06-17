package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.users.Administrador;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
import pe.edu.pe.pucp.proyecto.users.EstadoUsuario;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.web.dto.UsuarioDTO;

import java.util.ArrayList;
import java.util.Set;

/**
 * Conversión Usuario (entidad del modelo) -> UsuarioDTO (contrato plano).
 * Tolera null en todos los campos. NUNCA copia el password al DTO de salida.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    /** Entidad -> DTO. El password NO se copia (el DTO ni siquiera lo tiene). */
    public static UsuarioDTO toDTO(Usuario u) {
        if (u == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getIdUsuario());
        dto.setCorreo(u.getCorreo());
        dto.setNombre(u.getNombre());
        dto.setApellidoPaterno(u.getApellidoPaterno());
        dto.setApellidoMaterno(u.getApellidoMaterno());
        dto.setTelefono(u.getTelefono());
        dto.setPais(u.getPais());

        // Lista de roles a partir del Set<String> del modelo.
        Set<String> roles = u.getRoles();
        if (roles != null) {
            dto.setRoles(new ArrayList<>(roles));
        }

        dto.setTipoUsuario(resolverRolPrincipal(u));
        return dto;
    }

    /**
     * DTO -> Entidad para el REGISTRO (RF01). Siempre crea un {@link Invitado} (su
     * constructor ya asigna el rol "INVITADO"), nunca un Anfitrion/Administrador:
     * un registro público no puede auto-asignarse roles. El password va EN CLARO;
     * la BL ({@code UsuarioBL.insertar}) lo hashea con PasswordHasher antes de persistir.
     */
    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        Invitado u = new Invitado();   // el ctor ya hace agregarRol("INVITADO")
        u.setCorreo(dto.getCorreo());
        u.setPassword(dto.getPassword());   // en claro; la BL lo hashea
        u.setNombre(dto.getNombre());
        u.setApellidoPaterno(dto.getApellidoPaterno());
        u.setApellidoMaterno(dto.getApellidoMaterno());
        u.setTelefono(dto.getTelefono());

        // username: el DTO no lo trae; lo derivamos del correo (parte local) o del nombre.
        String correo = dto.getCorreo();
        String username;
        if (correo != null && correo.contains("@")) {
            username = correo.substring(0, correo.indexOf('@'));
        } else {
            username = dto.getNombre();
        }
        u.setUsername(username);

        // pais NOT NULL en BD: default "Perú" si el registro no lo trae.
        u.setPais(dto.getPais() != null && !dto.getPais().isBlank() ? dto.getPais() : "Perú");

        // CLAVE: el constructor no-arg de Usuario NO setea estadoActual (queda null), y el DAO
        // hace usuario.getEstadoActual().toString() en el INSERT -> NPE. Le damos un valor válido.
        // (No bloquea el login: la BL autentica solo por hash, sin mirar el estado.)
        u.setEstadoActual(EstadoUsuario.PENDIENTE_VALIDACION);
        u.setEstadoSesion(false);
        // estadoValidacion ya es "PENDIENTE" por el constructor; lo fijamos explícito por seguridad.
        u.setEstadoValidacion("PENDIENTE");

        // NOTA: tipoDocumento/numeroDocumento se dejan null a propósito: el DAO ya los inserta
        // como NULL por defecto (columnas nullable) y poner tipoDocumento=DNI dispararía la
        // validación de 8 dígitos de la BL (el form de registro no pide documento).
        return u;
    }

    /**
     * Deriva el rol principal. Prioriza el Set<roles>; si está vacío cae al
     * instanceof de la subclase. Orden de prioridad: ADMINISTRADOR > ANFITRION > INVITADO.
     */
    private static String resolverRolPrincipal(Usuario u) {
        Set<String> roles = u.getRoles();
        if (roles != null && !roles.isEmpty()) {
            if (roles.contains("ADMINISTRADOR")) {
                return "ADMINISTRADOR";
            }
            if (roles.contains("ANFITRION")) {
                return "ANFITRION";
            }
            if (roles.contains("INVITADO")) {
                return "INVITADO";
            }
        }
        // Fallback por subclase (Anfitrion extiende Cliente; Administrador es directo).
        if (u instanceof Administrador) {
            return "ADMINISTRADOR";
        }
        if (u instanceof Anfitrion) {
            return "ANFITRION";
        }
        return "INVITADO";
    }
}
