package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.users.Administrador;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
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
