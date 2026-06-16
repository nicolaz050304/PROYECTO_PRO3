package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
}
