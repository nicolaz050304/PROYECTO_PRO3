package pe.edu.pe.pucp.proyecto.web.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.regex.Pattern;

/**
 * Autorización del backend (defensa en profundidad: el backend NO confía solo en la UI).
 *
 * El frontend Blazor adjunta la identidad del usuario logueado en cada petición sensible
 * con las cabeceras {@code X-Usuario-Id} y {@code X-Usuario-Rol}. Este filtro intercepta TODAS
 * las peticiones y, para las rutas marcadas como solo-admin, exige rol ADMINISTRADOR:
 *   - sin identidad        -> 401 (no autenticado)
 *   - identidad no-admin   -> 403 (sin permiso)
 *   - admin                -> continúa
 *
 * El resto de rutas (públicas / de usuario) pasan sin tocar. Las reglas se amplían agregando
 * filas a REGLAS_ADMIN (misma idea para denuncias/incidencias/moderación/tipo de cambio).
 *
 * Nota: esto NO usa firma criptográfica (no es JWT); confía en que la cabecera la pone el
 * servidor Blazor (el navegador nunca habla directo con este backend). Para exposición pública
 * el siguiente paso sería un token firmado. Aun así, sube el listón: ya no basta con saltarse la UI.
 */
@Provider
public class AuthFilter implements ContainerRequestFilter {

    private static final String ROL_ADMIN = "ADMINISTRADOR";
    private static final String H_ID = "X-Usuario-Id";
    private static final String H_ROL = "X-Usuario-Rol";

    /** Regla: (método HTTP, patrón de ruta) que requiere ADMINISTRADOR. La ruta es relativa a webresources. */
    private record ReglaAdmin(String metodo, Pattern ruta) {}

    private static final ReglaAdmin[] REGLAS_ADMIN = {
            // Aprobar / rechazar la validación documentaria de un usuario (RF02).
            new ReglaAdmin("PUT", Pattern.compile("^ValidacionRS/\\d+/decision/?$")),
            // Dar de baja (lógica) a un usuario.
            new ReglaAdmin("DELETE", Pattern.compile("^UsuarioRS/\\d+/?$")),
            // Suspender / reactivar una cuenta de usuario.
            new ReglaAdmin("PUT", Pattern.compile("^UsuarioRS/\\d+/estado/?$")),
    };

    @Override
    public void filter(ContainerRequestContext ctx) {
        String metodo = ctx.getMethod();
        String ruta = ctx.getUriInfo().getPath();   // p. ej. "UsuarioRS/3" o "ValidacionRS/5/decision"
        if (ruta == null) {
            return;
        }
        // ¿La ruta+método están marcados como solo-admin?
        boolean requiereAdmin = false;
        for (ReglaAdmin r : REGLAS_ADMIN) {
            if (r.metodo().equalsIgnoreCase(metodo) && r.ruta().matcher(ruta).matches()) {
                requiereAdmin = true;
                break;
            }
        }
        if (!requiereAdmin) {
            return; // ruta pública / de usuario: no se valida aquí
        }

        String id = ctx.getHeaderString(H_ID);
        String rol = ctx.getHeaderString(H_ROL);

        // Sin identidad -> 401. La cabecera la pone el servidor Blazor con el usuario logueado.
        if (id == null || id.isBlank() || rol == null || rol.isBlank()) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"No autenticado: falta identidad para esta acción.\"}")
                    .type("application/json").build());
            return;
        }
        // Identidad presente pero sin rol de admin -> 403.
        if (!ROL_ADMIN.equalsIgnoreCase(rol.trim())) {
            ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"No autorizado: esta acción es solo para administradores.\"}")
                    .type("application/json").build());
        }
    }
}
