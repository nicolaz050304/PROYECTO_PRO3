package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.favorito.bl.FavoritoBL;
import pe.edu.pe.pucp.proyecto.favorito.implbl.FavoritoBLImpl;

import java.util.List;

/**
 * Recurso REST de Favoritos (RF27): persistencia de los alojamientos favoritos por usuario.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/FavoritoRS
 */
@Path("FavoritoRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FavoritoRS {

    private final FavoritoBL favoritoBL = new FavoritoBLImpl();

    /** GET FavoritoRS/usuario/{id} -> ids de alojamientos favoritos del usuario. */
    @GET
    @Path("usuario/{id}")
    public List<Integer> listar(@PathParam("id") int idUsuario) {
        return favoritoBL.listarPorUsuario(idUsuario);
    }

    /** POST FavoritoRS -> marca un favorito. Body { idUsuario, idAlojamiento }. */
    @POST
    public Response agregar(FavRequest req) {
        if (req == null || req.getIdUsuario() <= 0 || req.getIdAlojamiento() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"idUsuario e idAlojamiento son obligatorios\"}").build();
        }
        favoritoBL.agregar(req.getIdUsuario(), req.getIdAlojamiento());
        return Response.status(Response.Status.CREATED).build();
    }

    /** DELETE FavoritoRS/usuario/{u}/alojamiento/{a} -> quita un favorito. */
    @DELETE
    @Path("usuario/{u}/alojamiento/{a}")
    public Response quitar(@PathParam("u") int idUsuario, @PathParam("a") int idAlojamiento) {
        favoritoBL.quitar(idUsuario, idAlojamiento);
        return Response.noContent().build();
    }

    /** Body del POST: { "idUsuario": 1, "idAlojamiento": 5 }. */
    public static class FavRequest {
        private int idUsuario;
        private int idAlojamiento;
        public int getIdUsuario() { return idUsuario; }
        public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
        public int getIdAlojamiento() { return idAlojamiento; }
        public void setIdAlojamiento(int idAlojamiento) { this.idAlojamiento = idAlojamiento; }
    }
}
