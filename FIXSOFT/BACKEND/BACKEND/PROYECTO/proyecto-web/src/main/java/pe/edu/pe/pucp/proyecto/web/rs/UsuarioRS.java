package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.security.PasswordHasher;
import pe.edu.pe.pucp.proyecto.users.EstadoUsuario;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.LoginRequest;
import pe.edu.pe.pucp.proyecto.web.dto.UsuarioDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.UsuarioMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Usuario. Solo expone UsuarioDTO (nunca la entidad y nunca el
 * password) y delega toda la lógica en la BL: ningún acceso directo a datos.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/UsuarioRS
 */
@Path("UsuarioRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioRS {

    private final UsuarioBL usuarioBL = new UsuarioBLImpl();
    private final pe.edu.pe.pucp.proyecto.auditoria.bl.AuditoriaEstadoBL auditoriaBL =
            new pe.edu.pe.pucp.proyecto.auditoria.implbl.AuditoriaEstadoBLImpl();

    /** GET UsuarioRS -> todos los usuarios (sin password). Útil para admin/pruebas. */
    @GET
    public List<UsuarioDTO> listar() {
        List<UsuarioDTO> salida = new ArrayList<>();
        List<Usuario> lista = usuarioBL.listarTodos();
        if (lista != null) {
            for (Usuario u : lista) {
                salida.add(UsuarioMapper.toDTO(u));
            }
        }
        return salida;
    }

    /** GET UsuarioRS/{id} -> uno; 404 si no existe. */
    @GET
    @Path("{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        Usuario u = usuarioBL.obtenerPorId(id);
        if (u == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(UsuarioMapper.toDTO(u)).build();
    }

    /**
     * POST UsuarioRS -> registro de un nuevo usuario (INVITADO). RF01.
     * Recibe UsuarioDTO con el password EN CLARO (solo de entrada); la BL lo hashea.
     * Devuelve 201 + UsuarioDTO (sin password) o 400 con el mensaje de error.
     */
    @POST
    public Response crear(UsuarioDTO dto) {
        try {
            Usuario u = UsuarioMapper.toEntity(dto);
            int id = usuarioBL.insertar(u);   // valida y hashea el password
            u.setIdUsuario(id);
            return Response.status(Response.Status.CREATED)
                           .entity(UsuarioMapper.toDTO(u))   // toDTO NO incluye el password
                           .build();
        } catch (Exception e) {
            e.printStackTrace();   // stack trace completo en el log de GlassFish
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}")
                           .build();
        }
    }

    /**
     * POST UsuarioRS/login -> autenticación plana.
     * Recibe LoginRequest {correo, password} (el password SOLO existe en esta entrada,
     * jamás en una respuesta). Devuelve 200 + UsuarioDTO (sin password) si las
     * credenciales coinciden, o 401 si no.
     */
    @POST
    @Path("login")
    public Response login(LoginRequest credenciales) {
        if (credenciales == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Usuario u = usuarioBL.autenticar(credenciales.getCorreo(), credenciales.getPassword());
        if (u == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok(UsuarioMapper.toDTO(u)).build();
    }

    /**
     * PUT UsuarioRS/{id} -> actualiza los datos editables del perfil (RF01).
     * Cargamos el usuario EXISTENTE por id y seteamos solo los campos editables sobre él, para
     * NO perder su tipo/subclase, roles ni password al modificar (la BL reescribe todas las
     * columnas). Aquí no se cambian correo/password/tipo a propósito.
     */
    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, UsuarioDTO dto) {
        try {
            Usuario existente = usuarioBL.obtenerPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Usuario no encontrado\"}").build();
            }
            // Solo los campos editables del perfil (los null del DTO no pisan lo que ya había).
            if (dto.getNombre() != null)          existente.setNombre(dto.getNombre());
            if (dto.getApellidoPaterno() != null) existente.setApellidoPaterno(dto.getApellidoPaterno());
            if (dto.getApellidoMaterno() != null) existente.setApellidoMaterno(dto.getApellidoMaterno());
            if (dto.getTelefono() != null)        existente.setTelefono(dto.getTelefono());
            if (dto.getPais() != null)            existente.setPais(dto.getPais());

            usuarioBL.modificar(existente);
            return Response.ok(UsuarioMapper.toDTO(existente)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * DELETE UsuarioRS/{id} -> baja del usuario, para la gestión del admin.
     * La BL/DAO hace baja LÓGICA (lo deja SUSPENDIDO, no lo borra físicamente).
     */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        try {
            Usuario existente = usuarioBL.obtenerPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Usuario no encontrado\"}").build();
            }
            usuarioBL.eliminar(existente);
            return Response.ok("{\"ok\":true}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * PUT UsuarioRS/{id}/estado -> el admin cambia el estado de la cuenta (RF01).
     * Permite BLOQUEAR (SUSPENDIDO) o DESBLOQUEAR (DISPONIBLE) reutilizando modificar
     * (el UPDATE del DAO ya persiste estado_actual). Recibe { "estado": "SUSPENDIDO" }.
     */
    @PUT
    @Path("{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstado(@PathParam("id") int id, EstadoRequest req) {
        try {
            Usuario u = usuarioBL.obtenerPorId(id);
            if (u == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Usuario no encontrado\"}").build();
            }
            // Convertir el string recibido al enum; valida que sea un valor permitido.
            EstadoUsuario anterior = u.getEstadoActual();
            EstadoUsuario nuevo = EstadoUsuario.valueOf(req.getEstado());
            u.setEstadoActual(nuevo);
            usuarioBL.modificar(u);
            // RNF09: el admin suspende/reactiva la cuenta -> transición de estado_actual auditada.
            auditoriaBL.registrar(pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado.ENTIDAD_USUARIO,
                    id, "estado_actual", anterior != null ? anterior.name() : null, nuevo.name(),
                    "Cambio de estado por administrador");
            return Response.ok(UsuarioMapper.toDTO(u)).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Estado inválido\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /** Body del PUT {id}/estado: { "estado": "SUSPENDIDO" | "DISPONIBLE" | ... }. */
    public static class EstadoRequest {
        private String estado;
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }

    /**
     * POST UsuarioRS/recuperar -> recuperación de contraseña SIN correo (el proyecto no tiene SMTP):
     * valida la identidad por correo + número de documento (DNI/Pasaporte) y, si coinciden, fija una
     * nueva contraseña hasheada con BCrypt (PasswordHasher). Recibe
     * { "correo": "...", "documento": "...", "nuevaPassword": "..." }. Nunca devuelve datos del usuario.
     */
    @POST
    @Path("recuperar")
    public Response recuperar(RecuperarRequest req) {
        if (req == null || req.getCorreo() == null || req.getDocumento() == null || req.getNuevaPassword() == null
                || req.getCorreo().isBlank() || req.getDocumento().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Faltan datos.\"}").build();
        }
        if (req.getNuevaPassword().length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"La nueva contraseña debe tener al menos 6 caracteres.\"}").build();
        }

        // Buscar por correo (mismo enfoque que los finders: filtrar listarTodos, sin tocar la capa de datos).
        Usuario encontrado = null;
        List<Usuario> todos = usuarioBL.listarTodos();
        if (todos != null) {
            for (Usuario u : todos) {
                if (u != null && u.getCorreo() != null
                        && u.getCorreo().trim().equalsIgnoreCase(req.getCorreo().trim())) {
                    encontrado = u;
                    break;
                }
            }
        }

        // Identidad: correo + número de documento deben coincidir. Mensaje genérico (no revela cuál falló).
        if (encontrado == null || encontrado.getNumeroDocumento() == null
                || !encontrado.getNumeroDocumento().trim().equalsIgnoreCase(req.getDocumento().trim())) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Los datos no coinciden con ninguna cuenta.\"}").build();
        }

        // Solo cambia la contraseña (hasheada con BCrypt); el resto de la entidad se preserva en el update.
        encontrado.setPassword(PasswordHasher.hash(req.getNuevaPassword()));
        usuarioBL.modificar(encontrado);
        return Response.ok("{\"mensaje\":\"Contraseña actualizada.\"}").build();
    }

    /** Body del POST recuperar: { "correo", "documento", "nuevaPassword" }. */
    public static class RecuperarRequest {
        private String correo;
        private String documento;
        private String nuevaPassword;
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getDocumento() { return documento; }
        public void setDocumento(String documento) { this.documento = documento; }
        public String getNuevaPassword() { return nuevaPassword; }
        public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
    }

    /**
     * POST UsuarioRS/{id}/cambiar-password -> el usuario logueado cambia su propia contraseña.
     * Recibe { "passwordActual": "...", "nuevaPassword": "..." }. VERIFICA la contraseña actual
     * con BCrypt (PasswordHasher.verify) antes de fijar la nueva (también hasheada). Nunca devuelve
     * datos del usuario.
     */
    @POST
    @Path("{id}/cambiar-password")
    public Response cambiarPassword(@PathParam("id") int id, CambioPasswordRequest req) {
        if (req == null || req.getPasswordActual() == null || req.getNuevaPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"Faltan datos.\"}").build();
        }
        Usuario u = usuarioBL.obtenerPorId(id);
        if (u == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"error\":\"Usuario no encontrado.\"}").build();
        }
        // La contraseña actual debe verificar contra el hash almacenado.
        if (!PasswordHasher.verify(req.getPasswordActual(), u.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"La contraseña actual no es correcta.\"}").build();
        }
        if (req.getNuevaPassword().length() < 6) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"La nueva contraseña debe tener al menos 6 caracteres.\"}").build();
        }
        u.setPassword(PasswordHasher.hash(req.getNuevaPassword()));
        usuarioBL.modificar(u);
        return Response.ok("{\"mensaje\":\"Contraseña actualizada.\"}").build();
    }

    /** Body del POST {id}/cambiar-password: { "passwordActual", "nuevaPassword" }. */
    public static class CambioPasswordRequest {
        private String passwordActual;
        private String nuevaPassword;
        public String getPasswordActual() { return passwordActual; }
        public void setPasswordActual(String passwordActual) { this.passwordActual = passwordActual; }
        public String getNuevaPassword() { return nuevaPassword; }
        public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
    }
}
