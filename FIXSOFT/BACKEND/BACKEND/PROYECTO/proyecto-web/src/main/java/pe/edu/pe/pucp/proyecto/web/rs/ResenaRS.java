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

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.review.bl.ResenhaBL;
import pe.edu.pe.pucp.proyecto.review.implbl.ResenhaBLImpl;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.ResenaDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.ResenaMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de Reseña. Solo expone DTOs (nunca la entidad Resenha, cuya Reserva
 * tiene ciclos) y delega en la BL existente (ResenhaBL): ningún acceso directo a datos.
 *
 * El finder por alojamiento no está expuesto en ResenhaBL (sí en el DAO, pero la BL
 * no se toca), así que se resuelve aquí filtrando listarTodos() por el alojamientoId
 * que el mapper deriva vía la reserva.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/ResenaRS
 */
@Path("ResenaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResenaRS {

    private final ResenhaBL resenhaBL = new ResenhaBLImpl();
    private final ReservaBL reservaBL = new ReservaBLImpl();
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET ResenaRS -> todas las reseñas activas. */
    @GET
    public List<ResenaDTO> listar() {
        Map<Integer, Reserva> reservaCache = new HashMap<>();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ResenaDTO> salida = new ArrayList<>();
        List<Resenha> lista = resenhaBL.listarTodos();
        if (lista != null) {
            for (Resenha r : lista) {
                salida.add(ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                        reservaCache, aloCache, usuarioCache));
            }
        }
        return salida;
    }

    /** GET ResenaRS/{id} -> una; 404 si no existe. */
    @GET
    @Path("{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        Resenha r = resenhaBL.obtenerPorId(id);
        if (r == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResenaDTO dto = ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                new HashMap<>(), new HashMap<>(), new HashMap<>());
        return Response.ok(dto).build();
    }

    /**
     * GET ResenaRS/alojamiento/{id} -> reseñas de un alojamiento.
     * ResenhaBL no expone listarPorAlojamiento (sí el DAO, pero la BL no se toca),
     * así que se mapea listarTodos() y se filtra por el alojamientoId del DTO
     * (derivado por el mapper vía la reserva). Cachés compartidas (anti N+1).
     */
    @GET
    @Path("alojamiento/{id}")
    public List<ResenaDTO> listarPorAlojamiento(@PathParam("id") int alojamientoId) {
        Map<Integer, Reserva> reservaCache = new HashMap<>();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ResenaDTO> salida = new ArrayList<>();
        List<Resenha> lista = resenhaBL.listarTodos();
        if (lista != null) {
            for (Resenha r : lista) {
                ResenaDTO dto = ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                        reservaCache, aloCache, usuarioCache);
                if (dto != null && dto.getAlojamientoId() == alojamientoId) {
                    salida.add(dto);
                }
            }
        }
        return salida;
    }

    /**
     * GET ResenaRS/anfitrion/{id} -> reseñas que el huésped dejó AL ANFITRIÓN (objetivo='ANFITRION')
     * RF19. Se mapea listarTodos() y se filtra por objetivo + anfitrionId (derivado por el mapper vía
     * reserva->alojamiento->dueño). Cachés compartidas (anti N+1), igual que listarPorAlojamiento.
     */
    @GET
    @Path("anfitrion/{id}")
    public List<ResenaDTO> listarDeAnfitrion(@PathParam("id") int anfitrionId) {
        Map<Integer, Reserva> reservaCache = new HashMap<>();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ResenaDTO> salida = new ArrayList<>();
        List<Resenha> lista = resenhaBL.listarTodos();
        if (lista != null) {
            for (Resenha r : lista) {
                ResenaDTO dto = ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                        reservaCache, aloCache, usuarioCache);
                if (dto != null && "ANFITRION".equalsIgnoreCase(dto.getObjetivo())
                        && dto.getAnfitrionId() == anfitrionId) {
                    salida.add(dto);
                }
            }
        }
        return salida;
    }

    /**
     * GET ResenaRS/usuario/{id}/recibidas -> reseñas que RECIBIÓ un usuario como cliente,
     * es decir, las que le dejaron los anfitriones (tipo_autor='ANFITRION') en sus reservas
     * como invitado. Para el perfil público. Reutiliza el mismo toDTO y cachés (anti N+1).
     */
    @GET
    @Path("usuario/{id}/recibidas")
    public List<ResenaDTO> listarRecibidasPorCliente(@PathParam("id") int idUsuario) {
        List<ResenaDTO> salida = new ArrayList<>();
        try {
            var lista = resenhaBL.listarRecibidasPorCliente(idUsuario);
            // mismos cachés que usa listarPorAlojamiento:
            Map<Integer, Reserva> reservaCache = new HashMap<>();
            Map<Integer, Alojamiento> aloCache = new HashMap<>();
            Map<Integer, String> usuarioCache = new HashMap<>();
            for (Resenha r : lista) {
                salida.add(ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                        reservaCache, aloCache, usuarioCache));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salida;
    }

    /**
     * GET ResenaRS/todas -> TODAS las reseñas, incluidas las inactivas (ocultas).
     * Para moderación admin: el ResenaDTO lleva 'activo', así el frontend distingue
     * activas de ocultas. Cachés compartidas (anti N+1), igual que el GET normal.
     */
    @GET
    @Path("todas")
    public List<ResenaDTO> listarTodasIncluidasInactivas() {
        Map<Integer, Reserva> reservaCache = new HashMap<>();
        Map<Integer, Alojamiento> aloCache = new HashMap<>();
        Map<Integer, String> usuarioCache = new HashMap<>();
        List<ResenaDTO> salida = new ArrayList<>();
        List<Resenha> lista = resenhaBL.listarTodasIncluidasInactivas();
        if (lista != null) {
            for (Resenha r : lista) {
                salida.add(ResenaMapper.toDTO(r, reservaBL, alojamientoBL, usuarioBL,
                        reservaCache, aloCache, usuarioCache));
            }
        }
        return salida;
    }

    /** PUT ResenaRS/{id}/reactivar -> reactiva (activo=1) una reseña oculta. */
    @PUT
    @Path("{id}/reactivar")
    public Response reactivar(@PathParam("id") int id) {
        Resenha r = new Resenha();
        r.setIdResenha(id);
        resenhaBL.reactivar(r);
        return Response.ok().build();
    }

    /** POST ResenaRS -> crea; devuelve el DTO creado con su id. */
    @POST
    public Response crear(ResenaDTO dto) {
        Resenha entidad = ResenaMapper.toEntity(dto);
        int nuevoId = resenhaBL.insertar(entidad);
        entidad.setIdResenha(nuevoId);
        ResenaDTO creado = ResenaMapper.toDTO(entidad, reservaBL, alojamientoBL, usuarioBL,
                new HashMap<>(), new HashMap<>(), new HashMap<>());
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    /** PUT ResenaRS/{id} -> modifica (calificación/comentario). */
    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, ResenaDTO dto) {
        Resenha entidad = ResenaMapper.toEntity(dto);
        entidad.setIdResenha(id);
        resenhaBL.modificar(entidad);
        ResenaDTO actualizado = ResenaMapper.toDTO(entidad, reservaBL, alojamientoBL, usuarioBL,
                new HashMap<>(), new HashMap<>(), new HashMap<>());
        return Response.ok(actualizado).build();
    }

    /** DELETE ResenaRS/{id} -> baja lógica (activo=0) vía BL. */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        Resenha r = new Resenha();
        r.setIdResenha(id);
        resenhaBL.eliminar(r);
        return Response.noContent().build();
    }
}
