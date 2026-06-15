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
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.AlojamientoDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.AlojamientoMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de Alojamiento. Solo expone DTOs (nunca entidades del modelo) y
 * delega toda la lógica en la BL existente: ningún acceso directo a datos.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/AlojamientoRS
 */
@Path("AlojamientoRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlojamientoRS {

    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /** GET AlojamientoRS -> todos los alojamientos. */
    @GET
    public List<AlojamientoDTO> listar() {
        Map<Integer, String> cache = new HashMap<>();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> lista = alojamientoBL.listarTodos();
        if (lista != null) {
            for (Alojamiento al : lista) {
                salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache));
            }
        }
        return salida;
    }

    /** GET AlojamientoRS/{id} -> uno; 404 si no existe. */
    @GET
    @Path("{id}")
    public Response obtenerPorId(@PathParam("id") int id) {
        Alojamiento al = alojamientoBL.obtenerPorId(id);
        if (al == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(AlojamientoMapper.toDTO(al, usuarioBL, new HashMap<>())).build();
    }

    /**
     * GET AlojamientoRS/anfitrion/{id} -> alojamientos de un anfitrión.
     * No existe finder en la BL/DAO: se resuelve filtrando listarTodos() por
     * duenho.idUsuario, sin tocar la capa de datos.
     */
    @GET
    @Path("anfitrion/{id}")
    public List<AlojamientoDTO> listarPorAnfitrion(@PathParam("id") int anfitrionId) {
        Map<Integer, String> cache = new HashMap<>();
        List<AlojamientoDTO> salida = new ArrayList<>();
        List<Alojamiento> lista = alojamientoBL.listarTodos();
        if (lista != null) {
            for (Alojamiento al : lista) {
                if (al.getDuenho() != null && al.getDuenho().getIdUsuario() == anfitrionId) {
                    salida.add(AlojamientoMapper.toDTO(al, usuarioBL, cache));
                }
            }
        }
        return salida;
    }

    /** POST AlojamientoRS -> crea; devuelve el DTO creado con su id. */
    @POST
    public Response crear(AlojamientoDTO dto) {
        Alojamiento entidad = AlojamientoMapper.toEntity(dto);
        int nuevoId = alojamientoBL.insertar(entidad);
        entidad.setIdAlojamiento(nuevoId);
        return Response.status(Response.Status.CREATED)
                .entity(AlojamientoMapper.toDTO(entidad, usuarioBL, new HashMap<>()))
                .build();
    }

    /** PUT AlojamientoRS/{id} -> modifica. */
    @PUT
    @Path("{id}")
    public Response actualizar(@PathParam("id") int id, AlojamientoDTO dto) {
        Alojamiento entidad = AlojamientoMapper.toEntity(dto);
        entidad.setIdAlojamiento(id);
        alojamientoBL.modificar(entidad);
        return Response.ok(AlojamientoMapper.toDTO(entidad, usuarioBL, new HashMap<>())).build();
    }

    /** DELETE AlojamientoRS/{id} -> baja lógica vía BL. */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        Alojamiento al = alojamientoBL.obtenerPorId(id);
        if (al == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        alojamientoBL.eliminar(al);
        return Response.noContent().build();
    }
}
