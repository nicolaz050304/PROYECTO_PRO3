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

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.CuentaBancariaBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.CuentaBancariaDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.CuentaBancariaMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Cuentas Bancarias del anfitrión (RF15). Expone DTOs planos y delega
 * en la BL existente (CuentaBancariaBL). Una cuenta nueva nace NO verificada hasta que
 * un admin/proceso la valide.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/CuentaBancariaRS
 */
@Path("CuentaBancariaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuentaBancariaRS {

    private final CuentaBancariaBL cuentaBL = new CuentaBancariaBLImpl();

    /**
     * GET CuentaBancariaRS/usuario/{idUsuario} -> cuentas de ese anfitrión.
     * Se filtra por id_usuario porque cada anfitrión solo ve/gestiona SUS cuentas.
     */
    @GET
    @Path("usuario/{idUsuario}")
    public List<CuentaBancariaDTO> listarPorUsuario(@PathParam("idUsuario") int idUsuario) {
        List<CuentaBancariaDTO> salida = new ArrayList<>();
        List<CuentaBancaria> lista = cuentaBL.listarPorUsuario(idUsuario);
        if (lista != null) {
            for (CuentaBancaria c : lista) {
                salida.add(CuentaBancariaMapper.toDTO(c));
            }
        }
        return salida;
    }

    /** POST CuentaBancariaRS -> registra una cuenta; 201 con el DTO creado, 400 ante error de negocio. */
    @POST
    public Response crear(CuentaBancariaDTO dto) {
        try {
            CuentaBancaria c = CuentaBancariaMapper.toEntity(dto);
            int id = cuentaBL.insertar(c);   // la BL valida CCI(20), saldo>=0, titular
            c.setIdCuenta(id);
            return Response.status(Response.Status.CREATED)
                           .entity(CuentaBancariaMapper.toDTO(c)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /** DELETE CuentaBancariaRS/{id} -> elimina una cuenta (la BL impide borrar con fondos). */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        try {
            CuentaBancaria c = cuentaBL.obtenerPorId(id);
            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Cuenta no encontrada\"}").build();
            }
            cuentaBL.eliminar(c);
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
